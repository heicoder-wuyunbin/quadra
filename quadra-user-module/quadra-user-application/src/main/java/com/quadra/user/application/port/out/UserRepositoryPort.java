package com.quadra.user.application.port.out;

import com.quadra.user.domain.model.User;

public interface UserRepositoryPort {
    /**
     * 保存聚合根（包含 User, UserProfile, UserSetting）
     * 需处理手机号重复异常（DuplicateKeyException兜底）
     */
    void save(User user);
    
    /**
     * 根据ID查询用户聚合根
     */
    User findById(Long id);
    
    /**
     * 更新用户聚合根（主要更新 UserProfile 或 UserSetting）
     */
    void update(User user);
    
    /**
     * 生成分布式ID（如雪花算法）
     */
    Long nextId();
}
