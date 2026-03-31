package com.quadra.recommend.adapter.out.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.recommend.adapter.out.persistence.entity.ContentFeatureDO;
import com.quadra.recommend.adapter.out.persistence.entity.UserFeatureDO;
import com.quadra.recommend.adapter.out.persistence.entity.UserActionLogDO;
import com.quadra.recommend.adapter.out.persistence.mapper.ContentFeatureMapper;
import com.quadra.recommend.adapter.out.persistence.mapper.UserFeatureMapper;
import com.quadra.recommend.adapter.out.persistence.mapper.UserActionLogMapper;
import com.quadra.recommend.application.port.out.FeatureRepositoryPort;
import com.quadra.recommend.domain.model.UserFeature;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 特征仓库实现
 */
@Repository
public class FeatureRepositoryImpl implements FeatureRepositoryPort {

    private final UserFeatureMapper userFeatureMapper;
    private final UserActionLogMapper userActionLogMapper;
    private final ContentFeatureMapper contentFeatureMapper;
    private final ObjectMapper objectMapper;

    public FeatureRepositoryImpl(
            UserFeatureMapper userFeatureMapper,
            UserActionLogMapper userActionLogMapper,
            ContentFeatureMapper contentFeatureMapper,
            ObjectMapper objectMapper) {
        this.userFeatureMapper = userFeatureMapper;
        this.userActionLogMapper = userActionLogMapper;
        this.contentFeatureMapper = contentFeatureMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public Long nextId() {
        return IdWorker.getId();
    }

    @Override
    @Transactional
    public void upsertUserFeature(UserFeature userFeature) {
        UserFeatureDO featureDO = convertToDO(userFeature);
        
        // 尝试插入，如果已存在则更新
        try {
            userFeatureMapper.insert(featureDO);
        } catch (DuplicateKeyException e) {
            // 用户ID已存在，执行更新
            LambdaQueryWrapper<UserFeatureDO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(UserFeatureDO::getUserId, userFeature.getUserId());
            userFeatureMapper.update(featureDO, wrapper);
        }
    }

    @Override
    public UserFeature findUserFeatureByUserId(Long userId) {
        LambdaQueryWrapper<UserFeatureDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFeatureDO::getUserId, userId);
        UserFeatureDO featureDO = userFeatureMapper.selectOne(wrapper);
        
        if (featureDO == null) {
            return null;
        }
        
        return convertToDomain(featureDO);
    }

    @Override
    public List<Long> findAllUserIds() {
        // 从行为日志表中获取所有不重复的用户ID
        LambdaQueryWrapper<UserActionLogDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(UserActionLogDO::getUserId);
        wrapper.groupBy(UserActionLogDO::getUserId);
        
        List<UserActionLogDO> logs = userActionLogMapper.selectList(wrapper);
        return logs.stream()
            .map(UserActionLogDO::getUserId)
            .distinct()
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void upsertContentFeature(com.quadra.recommend.domain.model.ContentFeature contentFeature) {
        ContentFeatureDO featureDO = convertToDO(contentFeature);
        try {
            contentFeatureMapper.insert(featureDO);
        } catch (DuplicateKeyException e) {
            LambdaUpdateWrapper<ContentFeatureDO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ContentFeatureDO::getTargetId, contentFeature.getTargetId())
                    .eq(ContentFeatureDO::getTargetType, contentFeature.getTargetType().name())
                    .set(ContentFeatureDO::getHeatScore, contentFeature.getHeatScore())
                    .set(ContentFeatureDO::getTagsSummary, featureDO.getTagsSummary())
                    .set(ContentFeatureDO::getAiEmbedding, contentFeature.getAiEmbedding())
                    .set(ContentFeatureDO::getUpdatedAt, contentFeature.getUpdatedAt());
            contentFeatureMapper.update(null, updateWrapper);
        }
    }

    private ContentFeatureDO convertToDO(com.quadra.recommend.domain.model.ContentFeature contentFeature) {
        ContentFeatureDO featureDO = new ContentFeatureDO();
        featureDO.setId(contentFeature.getId());
        featureDO.setTargetId(contentFeature.getTargetId());
        featureDO.setTargetType(contentFeature.getTargetType().name());
        featureDO.setHeatScore(contentFeature.getHeatScore());
        featureDO.setAiEmbedding(contentFeature.getAiEmbedding());
        featureDO.setVersion(contentFeature.getVersion());
        featureDO.setUpdatedAt(contentFeature.getUpdatedAt());

        if (contentFeature.getTagsSummary() != null) {
            try {
                featureDO.setTagsSummary(objectMapper.writeValueAsString(contentFeature.getTagsSummary()));
            } catch (JsonProcessingException e) {
            }
        }

        return featureDO;
    }

    private UserFeatureDO convertToDO(UserFeature userFeature) {
        UserFeatureDO featureDO = new UserFeatureDO();
        featureDO.setId(userFeature.getId());
        featureDO.setUserId(userFeature.getUserId());
        featureDO.setActiveScore(userFeature.getActiveScore());
        featureDO.setVersion(userFeature.getVersion());
        featureDO.setUpdatedAt(userFeature.getUpdatedAt());
        
        // 将 Map 转换为 JSON 字符串
        if (userFeature.getTagsSummary() != null) {
            try {
                featureDO.setTagsSummary(objectMapper.writeValueAsString(userFeature.getTagsSummary()));
            } catch (JsonProcessingException e) {
                // 忽略序列化错误
            }
        }
        
        if (userFeature.getAiEmbedding() != null) {
            featureDO.setAiEmbedding(userFeature.getAiEmbedding());
        }
        
        return featureDO;
    }

    private UserFeature convertToDomain(UserFeatureDO featureDO) {
        try {
            UserFeature feature = UserFeature.create(featureDO.getId(), featureDO.getUserId());
            
            Map<String, Object> tagsSummary = null;
            if (featureDO.getTagsSummary() != null) {
                tagsSummary = objectMapper.readValue(
                    featureDO.getTagsSummary(), 
                    new TypeReference<Map<String, Object>>() {}
                );
            }
            
            feature.build(
                featureDO.getActiveScore(),
                tagsSummary,
                featureDO.getAiEmbedding()
            );
            
            return feature;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert UserFeatureDO to domain", e);
        }
    }
}
