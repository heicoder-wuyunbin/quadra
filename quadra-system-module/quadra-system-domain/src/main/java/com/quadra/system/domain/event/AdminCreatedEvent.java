package com.quadra.system.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class AdminCreatedEvent implements DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    
    private final Long adminId;
    private final String username;

    public AdminCreatedEvent(Long adminId, String username) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.adminId = adminId;
        this.username = username;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    public Long getAdminId() {
        return adminId;
    }

    public String getUsername() {
        return username;
    }
}
