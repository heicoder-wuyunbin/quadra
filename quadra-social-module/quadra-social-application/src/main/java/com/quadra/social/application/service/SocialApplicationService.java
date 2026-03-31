package com.quadra.social.application.service;

import com.quadra.social.application.port.in.FollowUserUseCase;
import com.quadra.social.application.port.in.UnfollowUserUseCase;
import com.quadra.social.application.port.in.command.FollowUserCommand;
import com.quadra.social.application.port.in.command.UnfollowUserCommand;
import com.quadra.social.application.port.out.FollowRepositoryPort;
import com.quadra.social.domain.exception.DomainException;
import com.quadra.social.domain.model.UserFollow;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SocialApplicationService implements FollowUserUseCase, UnfollowUserUseCase {

    private final FollowRepositoryPort followRepositoryPort;

    public SocialApplicationService(FollowRepositoryPort followRepositoryPort) {
        this.followRepositoryPort = followRepositoryPort;
    }

    @Override
    @Transactional
    public void follow(FollowUserCommand command) {
        // 1. 检查是否已关注
        UserFollow existingFollow = followRepositoryPort.findByUserIdAndTargetUserId(
            command.userId(), command.targetUserId());
        
        if (existingFollow != null && existingFollow.getDeleted() == 0) {
            throw new DomainException("已经关注过该用户");
        }
        
        // 2. 创建关注关系
        Long id = followRepositoryPort.nextId();
        UserFollow userFollow = UserFollow.follow(id, command.userId(), command.targetUserId());
        
        // 3. 如果之前取消过关注，则更新；否则新增
        if (existingFollow != null) {
            // 重新关注
            followRepositoryPort.update(userFollow);
        } else {
            followRepositoryPort.save(userFollow);
        }
    }

    @Override
    @Transactional
    public void unfollow(UnfollowUserCommand command) {
        // 1. 查询关注关系
        UserFollow userFollow = followRepositoryPort.findByUserIdAndTargetUserId(
            command.userId(), command.targetUserId());
        
        if (userFollow == null || userFollow.getDeleted() == 1) {
            throw new DomainException("未关注该用户");
        }
        
        // 2. 取消关注
        userFollow.unfollow();
        followRepositoryPort.update(userFollow);
    }
}
