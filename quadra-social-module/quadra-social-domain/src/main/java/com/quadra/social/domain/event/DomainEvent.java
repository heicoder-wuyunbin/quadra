package com.quadra.social.domain.event;

import java.time.LocalDateTime;

public interface DomainEvent {
    String getEventId();
    LocalDateTime getOccurredOn();
}
