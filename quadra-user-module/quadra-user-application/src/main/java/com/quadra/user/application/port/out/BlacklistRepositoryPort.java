package com.quadra.user.application.port.out;

import com.quadra.user.domain.model.blacklist.UserBlacklist;

public interface BlacklistRepositoryPort {
    /**
     * 保存黑名单关系
     */
    void save(UserBlacklist blacklist);
    
    /**
     * 取消拉黑（物理删除或逻辑删除均可，由适配器决定）
     */
    void remove(Long userId, Long targetUserId);
    
    /**
     * 检查是否已拉黑
     */
    boolean exists(Long userId, Long targetUserId);
    
    /**
     * 生成分布式ID
     */
    Long nextId();
}
