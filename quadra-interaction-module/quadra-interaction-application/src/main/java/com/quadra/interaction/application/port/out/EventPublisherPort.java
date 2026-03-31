package com.quadra.interaction.application.port.out;

import com.quadra.interaction.domain.event.DomainEvent;
import java.util.List;

/**
 * 事件发布端口
 */
public interface EventPublisherPort {
    /**
     * 将领域事件写入 Outbox 表，保证与业务操作在同一个本地事务中
     */
    void publish(List<DomainEvent> events);
}
