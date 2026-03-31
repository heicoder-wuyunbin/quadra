package com.quadra.recommend.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 特征构建完成事件
 * 当用户或内容特征构建完成时发布
 */
public class FeatureBuiltEvent implements DomainEvent {
    
    private final String eventId;
    private final LocalDateTime occurredOn;
    private final Long targetId;
    private final String targetType; // USER or CONTENT
    private final String featureType; // USER_FEATURE or CONTENT_FEATURE

    public FeatureBuiltEvent(Long targetId, String targetType, String featureType) {
        this.eventId = UUID.randomUUID().toString().replace("-", "");
        this.occurredOn = LocalDateTime.now();
        this.targetId = targetId;
        this.targetType = targetType;
        this.featureType = featureType;
    }

    @Override
    public String getEventId() {
        return eventId;
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredOn;
    }

    public Long getTargetId() {
        return targetId;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getFeatureType() {
        return featureType;
    }
}
