package com.quadra.user.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserRegisteredEvent implements DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    
    private final Long userId;
    private final String mobile;

    public UserRegisteredEvent(Long userId, String mobile) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.userId = userId;
        this.mobile = mobile;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    public Long getUserId() {
        return userId;
    }

    public String getMobile() {
        return mobile;
    }
}
