package com.quadra.interaction.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

import com.quadra.interaction.domain.model.ActionType;
import com.quadra.interaction.domain.model.TargetType;

/**
 * 互动创建事件
 * 用于通知 content 服务更新计数
 */
public class InteractionCreatedEvent implements DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    
    private final Long interactionId;
    private final Long userId;
    private final Long targetId;
    private final TargetType targetType;
    private final ActionType actionType;

    public InteractionCreatedEvent(Long interactionId, Long userId, Long targetId, 
                                    TargetType targetType, ActionType actionType) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.interactionId = interactionId;
        this.userId = userId;
        this.targetId = targetId;
        this.targetType = targetType;
        this.actionType = actionType;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    public Long getInteractionId() {
        return interactionId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getTargetId() {
        return targetId;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public ActionType getActionType() {
        return actionType;
    }
}
