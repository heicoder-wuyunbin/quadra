package com.quadra.content.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class VideoPublishedEvent implements DomainEvent {
    private final String eventId;
    private final LocalDateTime occurredOn;
    
    private final Long videoId;
    private final Long userId;

    public VideoPublishedEvent(Long videoId, Long userId) {
        this.eventId = UUID.randomUUID().toString();
        this.occurredOn = LocalDateTime.now();
        this.videoId = videoId;
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

    public Long getVideoId() {
        return videoId;
    }

    public Long getUserId() {
        return userId;
    }
}
