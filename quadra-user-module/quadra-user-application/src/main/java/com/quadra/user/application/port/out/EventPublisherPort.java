package com.quadra.user.application.port.out;

import com.quadra.user.domain.event.DomainEvent;
import java.util.List;

public interface EventPublisherPort {
    /**
     * 将领域事件写入 Outbox 表，保证与业务操作在同一个本地事务中
     */
    void publish(List<DomainEvent> events);
}
