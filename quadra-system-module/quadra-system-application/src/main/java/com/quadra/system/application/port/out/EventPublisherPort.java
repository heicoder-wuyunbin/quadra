package com.quadra.system.application.port.out;

import com.quadra.system.domain.event.DomainEvent;
import java.util.List;

public interface EventPublisherPort {
    void publish(List<DomainEvent> events);
}
