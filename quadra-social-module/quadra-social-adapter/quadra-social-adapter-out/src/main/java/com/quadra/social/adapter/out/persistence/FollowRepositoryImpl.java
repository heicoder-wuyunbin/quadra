package com.quadra.social.adapter.out.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.quadra.social.adapter.out.persistence.entity.UserFollowDO;
import com.quadra.social.adapter.out.persistence.mapper.UserFollowMapper;
import com.quadra.social.application.port.out.FollowRepositoryPort;
import com.quadra.social.domain.model.UserFollow;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class FollowRepositoryImpl implements FollowRepositoryPort {

    private final UserFollowMapper userFollowMapper;

    public FollowRepositoryImpl(UserFollowMapper userFollowMapper) {
        this.userFollowMapper = userFollowMapper;
    }

    @Override
    @Transactional
    public void save(UserFollow userFollow) {
        UserFollowDO userFollowDO = new UserFollowDO();
        userFollowDO.setId(userFollow.getId());
        userFollowDO.setUserId(userFollow.getUserId());
        userFollowDO.setTargetUserId(userFollow.getTargetUserId());
        userFollowDO.setVersion(userFollow.getVersion());
        userFollowDO.setDeleted(userFollow.getDeleted());
        userFollowDO.setCreatedAt(userFollow.getCreatedAt());
        userFollowDO.setUpdatedAt(userFollow.getUpdatedAt());
        userFollowMapper.insert(userFollowDO);
    }

    @Override
    public UserFollow findByUserIdAndTargetUserId(Long userId, Long targetUserId) {
        LambdaQueryWrapper<UserFollowDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollowDO::getUserId, userId)
               .eq(UserFollowDO::getTargetUserId, targetUserId);
        UserFollowDO userFollowDO = userFollowMapper.selectOne(wrapper);
        if (userFollowDO == null) {
            return null;
        }
        return convertToDomain(userFollowDO);
    }

    @Override
    @Transactional
    public void update(UserFollow userFollow) {
        UserFollowDO userFollowDO = new UserFollowDO();
        userFollowDO.setId(userFollow.getId());
        userFollowDO.setUserId(userFollow.getUserId());
        userFollowDO.setTargetUserId(userFollow.getTargetUserId());
        userFollowDO.setVersion(userFollow.getVersion());
        userFollowDO.setDeleted(userFollow.getDeleted());
        userFollowDO.setUpdatedAt(userFollow.getUpdatedAt());
        userFollowMapper.updateById(userFollowDO);
    }

    @Override
    public Long nextId() {
        return IdWorker.getId();
    }

    private UserFollow convertToDomain(UserFollowDO userFollowDO) {
        try {
            java.lang.reflect.Constructor<UserFollow> constructor = UserFollow.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            UserFollow userFollow = constructor.newInstance();

            java.lang.reflect.Field idField = UserFollow.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(userFollow, userFollowDO.getId());

            java.lang.reflect.Field userIdField = UserFollow.class.getDeclaredField("userId");
            userIdField.setAccessible(true);
            userIdField.set(userFollow, userFollowDO.getUserId());

            java.lang.reflect.Field targetUserIdField = UserFollow.class.getDeclaredField("targetUserId");
            targetUserIdField.setAccessible(true);
            targetUserIdField.set(userFollow, userFollowDO.getTargetUserId());

            java.lang.reflect.Field versionField = UserFollow.class.getDeclaredField("version");
            versionField.setAccessible(true);
            versionField.set(userFollow, userFollowDO.getVersion());

            java.lang.reflect.Field deletedField = UserFollow.class.getDeclaredField("deleted");
            deletedField.setAccessible(true);
            deletedField.set(userFollow, userFollowDO.getDeleted());

            java.lang.reflect.Field createdAtField = UserFollow.class.getDeclaredField("createdAt");
            createdAtField.setAccessible(true);
            createdAtField.set(userFollow, userFollowDO.getCreatedAt());

            java.lang.reflect.Field updatedAtField = UserFollow.class.getDeclaredField("updatedAt");
            updatedAtField.setAccessible(true);
            updatedAtField.set(userFollow, userFollowDO.getUpdatedAt());

            return userFollow;
        } catch (Exception e) {
            throw new RuntimeException("Failed to restore UserFollow from DB", e);
        }
    }
}
