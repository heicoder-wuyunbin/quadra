package com.quadra.recommend.adapter.out.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.recommend.adapter.out.persistence.entity.OutboxEventDO;
import com.quadra.recommend.adapter.out.persistence.mapper.OutboxEventMapper;
import com.quadra.recommend.application.port.out.EventPublisherPort;
import com.quadra.recommend.domain.event.DomainEvent;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Outbox 事件仓库实现
 */
@Repository
public class OutboxEventRepositoryImpl implements EventPublisherPort {

    private final OutboxEventMapper outboxEventMapper;
    private final ObjectMapper objectMapper;

    public OutboxEventRepositoryImpl(
            OutboxEventMapper outboxEventMapper,
            ObjectMapper objectMapper) {
        this.outboxEventMapper = outboxEventMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void publish(List<DomainEvent> events) {
        for (DomainEvent event : events) {
            OutboxEventDO outboxEvent = new OutboxEventDO();
            outboxEvent.setEventId(event.getEventId());
            outboxEvent.setEventType(event.getClass().getSimpleName());
            outboxEvent.setAggregateType("RECOMMEND");
            outboxEvent.setStatus(0); // 待投递
            outboxEvent.setRetryCount(0);
            outboxEvent.setCreatedAt(LocalDateTime.now());
            
            try {
                outboxEvent.setPayload(objectMapper.writeValueAsString(event));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize event", e);
            }
            
            outboxEventMapper.insert(outboxEvent);
        }
    }
}
