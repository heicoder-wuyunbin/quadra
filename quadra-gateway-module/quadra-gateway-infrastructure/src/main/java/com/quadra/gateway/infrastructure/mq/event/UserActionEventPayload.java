package com.quadra.gateway.infrastructure.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户行为事件载荷
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActionEventPayload implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户 ID
     */
    private Long userId;
    
    /**
     * 目标 ID
     */
    private Long targetId;
    
    /**
     * 目标类型：USER, MOVEMENT, VIDEO
     */
    private String targetType;
    
    /**
     * 动作类型：VIEW, LIKE, SKIP, DISLIKE
     */
    private String actionType;
    
    /**
     * 权重分数
     */
    private Double weight;
}
