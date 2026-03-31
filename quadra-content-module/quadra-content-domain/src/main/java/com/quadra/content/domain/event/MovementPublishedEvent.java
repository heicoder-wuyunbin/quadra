package com.quadra.content.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class MovementPublishedEvent implements DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    
    private final Long movementId;
    private final Long userId;

    public MovementPublishedEvent(Long movementId, Long userId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.movementId = movementId;
        this.userId = userId;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    public Long getMovementId() {
        return movementId;
    }

    public Long getUserId() {
        return userId;
    }
}
