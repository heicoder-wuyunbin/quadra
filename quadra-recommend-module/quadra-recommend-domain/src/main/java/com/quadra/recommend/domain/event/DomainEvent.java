package com.quadra.recommend.domain.event;

import java.time.LocalDateTime;

/**
 * 领域事件接口
 * 所有领域事件的标记接口
 */
public interface DomainEvent {
    /**
     * 获取事件ID
     */
    String getEventId();

    /**
     * 获取事件发生时间
     */
    LocalDateTime getOccurredOn();
}
