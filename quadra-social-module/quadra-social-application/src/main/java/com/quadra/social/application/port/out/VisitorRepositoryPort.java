package com.quadra.social.application.port.out;

import com.quadra.social.domain.model.UserVisitor;

public interface VisitorRepositoryPort {
    /**
     * 保存访客记录
     */
    void save(UserVisitor visitor);
    
    /**
     * 生成分布式ID
     */
    Long nextId();
}
