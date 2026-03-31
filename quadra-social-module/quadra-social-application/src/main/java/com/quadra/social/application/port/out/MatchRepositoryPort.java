package com.quadra.social.application.port.out;

import com.quadra.social.domain.model.UserMatchLike;

public interface MatchRepositoryPort {
    /**
     * 保存滑动记录
     */
    void save(UserMatchLike matchLike);
    
    /**
     * 查询目标用户对当前用户的操作记录
     */
    UserMatchLike findTargetLikeUser(Long targetUserId, Long userId);
    
    /**
     * 更新滑动记录
     */
    void update(UserMatchLike matchLike);
    
    /**
     * 生成分布式ID
     */
    Long nextId();
}
