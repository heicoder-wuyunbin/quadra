package com.quadra.social.application.port.out;

import com.quadra.social.domain.model.UserFollow;

public interface FollowRepositoryPort {
    /**
     * 保存关注关系
     */
    void save(UserFollow userFollow);
    
    /**
     * 根据用户ID和目标用户ID查询关注关系
     */
    UserFollow findByUserIdAndTargetUserId(Long userId, Long targetUserId);
    
    /**
     * 更新关注关系
     */
    void update(UserFollow userFollow);
    
    /**
     * 生成分布式ID
     */
    Long nextId();
}
