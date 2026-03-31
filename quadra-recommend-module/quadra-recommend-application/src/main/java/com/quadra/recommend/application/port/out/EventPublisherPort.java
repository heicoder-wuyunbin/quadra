package com.quadra.recommend.application.port.out;

import com.quadra.recommend.domain.event.DomainEvent;
import java.util.List;

/**
 * 事件发布端口
 */
public interface EventPublisherPort {
    
    /**
     * 发布领域事件到 Outbox
     * @param events 领域事件列表
     */
    void publish(List<DomainEvent> events);
}
