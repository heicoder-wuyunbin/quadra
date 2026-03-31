package com.quadra.content.application.port.out;

import com.quadra.content.domain.model.Movement;

/**
 * 动态仓储端口
 */
public interface MovementRepositoryPort {
    /**
     * 保存动态
     */
    void save(Movement movement);
    
    /**
     * 根据ID查询动态
     */
    Movement findById(Long id);
    
    /**
     * 更新动态
     */
    void update(Movement movement);
    
    /**
     * 生成分布式ID
     */
    Long nextId();
}
