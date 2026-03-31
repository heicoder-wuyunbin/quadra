package com.quadra.social.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class MatchCreatedEvent implements DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    
    private final Long matchId;
    private final Long userId;
    private final Long targetUserId;

    public MatchCreatedEvent(Long matchId, Long userId, Long targetUserId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.matchId = matchId;
        this.userId = userId;
        this.targetUserId = targetUserId;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    public Long getMatchId() {
        return matchId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }
}
