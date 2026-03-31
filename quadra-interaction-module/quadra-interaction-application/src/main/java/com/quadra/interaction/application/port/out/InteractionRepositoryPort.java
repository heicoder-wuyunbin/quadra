package com.quadra.interaction.application.port.out;

import com.quadra.interaction.domain.model.Interaction;
import com.quadra.interaction.domain.model.TargetType;

/**
 * 互动仓储端口
 */
public interface InteractionRepositoryPort {
    /**
     * 保存互动聚合根
     */
    void save(Interaction interaction);

    /**
     * 更新互动聚合根
     */
    void update(Interaction interaction);
    
    /**
     * 取消点赞（逻辑删除）
     */
    void cancelLike(Long userId, TargetType targetType, Long targetId);
    
    /**
     * 根据ID查询互动
     */
    Interaction findById(Long id);
    
    /**
     * 生成分布式ID
     */
    Long nextId();
    
    /**
     * 检查是否已点赞
     */
    boolean existsLike(Long userId, TargetType targetType, Long targetId);
    
    /**
     * 查询点赞记录（用于取消）
     */
    Interaction findLike(Long userId, TargetType targetType, Long targetId);
}
