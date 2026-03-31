package com.quadra.recommend.application.service;

import com.quadra.recommend.application.port.in.BuildUserFeatureUseCase;
import com.quadra.recommend.application.port.in.BuildRecommendUserUseCase;
import com.quadra.recommend.application.port.out.FeatureRepositoryPort;
import com.quadra.recommend.application.port.out.RecommendRepositoryPort;
import com.quadra.recommend.domain.model.RecommendUser;
import com.quadra.recommend.domain.model.UserFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 特征应用服务
 * 实现构建用户特征和推荐结果用例
 */
@Service
public class FeatureApplicationService implements BuildUserFeatureUseCase, BuildRecommendUserUseCase {

    private static final Logger log = LoggerFactory.getLogger(FeatureApplicationService.class);
    
    private final FeatureRepositoryPort featureRepositoryPort;
    private final RecommendRepositoryPort recommendRepositoryPort;

    public FeatureApplicationService(
            FeatureRepositoryPort featureRepositoryPort,
            RecommendRepositoryPort recommendRepositoryPort) {
        this.featureRepositoryPort = featureRepositoryPort;
        this.recommendRepositoryPort = recommendRepositoryPort;
    }

    @Override
    @Transactional
    public void buildUserFeature(Long userId) {
        log.info("Building user feature for userId: {}", userId);
        
        UserFeature feature = featureRepositoryPort.findUserFeatureByUserId(userId);
        if (feature == null) {
            Long id = featureRepositoryPort.nextId();
            feature = UserFeature.create(id, userId);
        }
        
        // 计算活跃度得分（基于行为日志统计，这里简化处理）
        // 实际实现中需要查询行为日志进行统计
        int activeScore = calculateActiveScore(userId);
        
        feature.build(activeScore, null, null);
        featureRepositoryPort.upsertUserFeature(feature);
        
        log.info("User feature built successfully for userId: {}", userId);
    }

    @Override
    @Transactional
    public void buildAllUserFeatures() {
        log.info("Building features for all users");
        
        List<Long> userIds = featureRepositoryPort.findAllUserIds();
        for (Long userId : userIds) {
            try {
                buildUserFeature(userId);
            } catch (Exception e) {
                log.error("Failed to build feature for userId: {}", userId, e);
            }
        }
        
        log.info("Completed building features for {} users", userIds.size());
    }

    @Override
    @Transactional
    public void buildRecommendUser(Long userId) {
        log.info("Building recommend users for userId: {}", userId);
        
        LocalDate today = LocalDate.now();
        
        // 删除今日已有的推荐结果
        recommendRepositoryPort.deleteTodayRecommendUsers(userId, today);
        
        // 生成推荐结果（这里简化处理，实际需要基于特征计算）
        List<RecommendUser> recommendations = generateRecommendations(userId, today);
        
        if (!recommendations.isEmpty()) {
            recommendRepositoryPort.saveAllRecommendUsers(recommendations);
        }
        
        log.info("Built {} recommend users for userId: {}", recommendations.size(), userId);
    }

    @Override
    @Transactional
    public void buildAllRecommendUsers() {
        log.info("Building recommend users for all users");
        
        List<Long> userIds = featureRepositoryPort.findAllUserIds();
        for (Long userId : userIds) {
            try {
                buildRecommendUser(userId);
            } catch (Exception e) {
                log.error("Failed to build recommend users for userId: {}", userId, e);
            }
        }
        
        log.info("Completed building recommend users for {} users", userIds.size());
    }

    /**
     * 计算用户活跃度得分（简化版本）
     * 实际实现中应该查询行为日志进行统计计算
     */
    private int calculateActiveScore(Long userId) {
        // 简化实现：返回一个默认值
        // 实际应该基于用户行为日志统计
        Random random = new Random(userId);
        return 50 + random.nextInt(50);
    }

    /**
     * 生成推荐用户列表（简化版本）
     * 实际实现中应该基于特征匹配和AI计算
     */
    private List<RecommendUser> generateRecommendations(Long userId, LocalDate recommendDate) {
        // 简化实现：生成随机推荐
        // 实际应该基于用户特征、内容特征和AI向量相似度计算
        List<Long> allUserIds = featureRepositoryPort.findAllUserIds();
        List<RecommendUser> recommendations = new ArrayList<>();
        
        Random random = new Random(userId + recommendDate.toEpochDay());
        Collections.shuffle(allUserIds, random);
        
        int count = Math.min(10, allUserIds.size());
        for (int i = 0; i < count; i++) {
            Long targetId = allUserIds.get(i);
            if (targetId.equals(userId)) {
                continue;
            }
            
            Long id = recommendRepositoryPort.nextId();
            BigDecimal score = BigDecimal.valueOf(50 + random.nextDouble() * 50)
                .setScale(2, RoundingMode.HALF_UP);
            
            recommendations.add(RecommendUser.create(id, userId, targetId, score, recommendDate));
        }
        
        return recommendations;
    }
}
