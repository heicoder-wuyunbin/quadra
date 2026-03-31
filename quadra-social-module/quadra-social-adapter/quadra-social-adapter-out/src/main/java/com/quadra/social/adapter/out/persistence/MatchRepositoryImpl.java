package com.quadra.social.adapter.out.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.quadra.social.adapter.out.persistence.entity.UserMatchLikeDO;
import com.quadra.social.adapter.out.persistence.mapper.UserMatchLikeMapper;
import com.quadra.social.application.port.out.MatchRepositoryPort;
import com.quadra.social.domain.model.UserMatchLike;
import com.quadra.social.domain.model.UserMatchLike.ActionType;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class MatchRepositoryImpl implements MatchRepositoryPort {

    private final UserMatchLikeMapper userMatchLikeMapper;

    public MatchRepositoryImpl(UserMatchLikeMapper userMatchLikeMapper) {
        this.userMatchLikeMapper = userMatchLikeMapper;
    }

    @Override
    @Transactional
    public void save(UserMatchLike matchLike) {
        UserMatchLikeDO matchLikeDO = convertToDO(matchLike);
        userMatchLikeMapper.insert(matchLikeDO);
    }

    @Override
    public UserMatchLike findTargetLikeUser(Long targetUserId, Long userId) {
        // 查询 targetUserId 对 userId 的操作记录
        LambdaQueryWrapper<UserMatchLikeDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMatchLikeDO::getUserId, targetUserId)
               .eq(UserMatchLikeDO::getTargetUserId, userId);
        UserMatchLikeDO matchLikeDO = userMatchLikeMapper.selectOne(wrapper);
        if (matchLikeDO == null) {
            return null;
        }
        return convertToDomain(matchLikeDO);
    }

    @Override
    @Transactional
    public void update(UserMatchLike matchLike) {
        UserMatchLikeDO matchLikeDO = convertToDO(matchLike);
        userMatchLikeMapper.updateById(matchLikeDO);
    }

    @Override
    public Long nextId() {
        return IdWorker.getId();
    }

    private UserMatchLikeDO convertToDO(UserMatchLike matchLike) {
        UserMatchLikeDO matchLikeDO = new UserMatchLikeDO();
        matchLikeDO.setId(matchLike.getId());
        matchLikeDO.setUserId(matchLike.getUserId());
        matchLikeDO.setTargetUserId(matchLike.getTargetUserId());
        matchLikeDO.setActionType(matchLike.getActionType().name());
        matchLikeDO.setMatchTime(matchLike.getMatchTime());
        matchLikeDO.setVersion(matchLike.getVersion());
        matchLikeDO.setDeleted(matchLike.getDeleted());
        matchLikeDO.setCreatedAt(matchLike.getCreatedAt());
        matchLikeDO.setUpdatedAt(matchLike.getUpdatedAt());
        return matchLikeDO;
    }

    private UserMatchLike convertToDomain(UserMatchLikeDO matchLikeDO) {
        try {
            java.lang.reflect.Constructor<UserMatchLike> constructor = UserMatchLike.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            UserMatchLike matchLike = constructor.newInstance();

            java.lang.reflect.Field idField = UserMatchLike.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(matchLike, matchLikeDO.getId());

            java.lang.reflect.Field userIdField = UserMatchLike.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(matchLike, matchLikeDO.getUserId());

            java.lang.reflect.Field targetUserIdField = UserMatchLike.class.getDeclaredField("targetUserId");
            targetUserIdField.setAccessible(true);
            targetUserIdField.set(matchLike, matchLikeDO.getTargetUserId());

            java.lang.reflect.Field actionTypeField = UserMatchLike.class.getDeclaredField("actionType");
            actionTypeField.setAccessible(true);
            actionTypeField.set(matchLike, ActionType.valueOf(matchLikeDO.getActionType()));

            java.lang.reflect.Field matchTimeField = UserMatchLike.class.getDeclaredField("matchTime");
            matchTimeField.setAccessible(true);
            matchTimeField.set(matchLike, matchLikeDO.getMatchTime());

            java.lang.reflect.Field versionField = UserMatchLike.class.getDeclaredField("version");
            versionField.setAccessible(true);
            versionField.set(matchLike, matchLikeDO.getVersion());

            java.lang.reflect.Field deletedField = UserMatchLike.class.getDeclaredField("deleted");
            deletedField.setAccessible(true);
            deletedField.set(matchLike, matchLikeDO.getDeleted());

            java.lang.reflect.Field createdAtField = UserMatchLike.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(matchLike, matchLikeDO.getCreatedAt());

            java.lang.reflect.Field updatedAtField = UserMatchLike.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(matchLike, matchLikeDO.getUpdatedAt());

            return matchLike;
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore UserMatchLike from DB", e);
        }
    }
}
