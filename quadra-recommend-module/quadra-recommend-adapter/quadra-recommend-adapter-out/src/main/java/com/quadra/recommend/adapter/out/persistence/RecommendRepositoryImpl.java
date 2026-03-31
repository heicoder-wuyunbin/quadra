package com.quadra.recommend.adapter.out.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.quadra.recommend.adapter.out.persistence.entity.RecommendUserDO;
import com.quadra.recommend.adapter.out.persistence.entity.RecommendContentDO;
import com.quadra.recommend.adapter.out.persistence.mapper.RecommendUserMapper;
import com.quadra.recommend.adapter.out.persistence.mapper.RecommendContentMapper;
import com.quadra.recommend.application.port.out.RecommendRepositoryPort;
import com.quadra.recommend.domain.model.RecommendUser;
import com.quadra.recommend.domain.model.RecommendContent;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * 推荐仓库实现
 */
@Repository
public class RecommendRepositoryImpl implements RecommendRepositoryPort {

    private final RecommendUserMapper recommendUserMapper;
    private final RecommendContentMapper recommendContentMapper;

    public RecommendRepositoryImpl(
            RecommendUserMapper recommendUserMapper,
            RecommendContentMapper recommendContentMapper) {
        this.recommendUserMapper = recommendUserMapper;
        this.recommendContentMapper = recommendContentMapper;
    }

    @Override
    public Long nextId() {
        return IdWorker.getId();
    }

    @Override
    @Transactional
    public void saveAllRecommendUsers(List<RecommendUser> recommendUsers) {
        for (RecommendUser recommendUser : recommendUsers) {
            RecommendUserDO recommendUserDO = convertToDO(recommendUser);
            recommendUserMapper.insert(recommendUserDO);
        }
    }

    @Override
    @Transactional
    public void saveAllRecommendContents(List<RecommendContent> recommendContents) {
        for (RecommendContent recommendContent : recommendContents) {
            RecommendContentDO recommendContentDO = convertToContentDO(recommendContent);
            recommendContentMapper.insert(recommendContentDO);
        }
    }

    @Override
    @Transactional
    public void deleteTodayRecommendUsers(Long userId, LocalDate recommendDate) {
        LambdaQueryWrapper<RecommendUserDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecommendUserDO::getUserId, userId)
               .eq(RecommendUserDO::getRecommendDate, recommendDate);
        recommendUserMapper.delete(wrapper);
    }

    private RecommendUserDO convertToDO(RecommendUser recommendUser) {
        RecommendUserDO recommendUserDO = new RecommendUserDO();
        recommendUserDO.setId(recommendUser.getId());
        recommendUserDO.setUserId(recommendUser.getUserId());
        recommendUserDO.setRecommendTargetId(recommendUser.getRecommendTargetId());
        recommendUserDO.setScore(recommendUser.getScore());
        recommendUserDO.setRecommendDate(recommendUser.getRecommendDate());
        recommendUserDO.setVersion(recommendUser.getVersion());
        recommendUserDO.setCreatedAt(recommendUser.getCreatedAt());
        return recommendUserDO;
    }

    private RecommendContentDO convertToContentDO(RecommendContent recommendContent) {
        RecommendContentDO recommendContentDO = new RecommendContentDO();
        recommendContentDO.setId(recommendContent.getId());
        recommendContentDO.setUserId(recommendContent.getUserId());
        recommendContentDO.setTargetId(recommendContent.getTargetId());
        recommendContentDO.setTargetType(recommendContent.getTargetType().name());
        recommendContentDO.setScore(recommendContent.getScore());
        recommendContentDO.setVersion(recommendContent.getVersion());
        recommendContentDO.setCreatedAt(recommendContent.getCreatedAt());
        return recommendContentDO;
    }
}
