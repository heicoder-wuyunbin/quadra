package com.quadra.system.adapter.out.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.system.adapter.out.persistence.entity.OutboxEventDO;
import com.quadra.system.adapter.out.persistence.mapper.OutboxEventMapper;
import com.quadra.system.application.port.out.EventPublisherPort;
import com.quadra.system.domain.event.DomainEvent;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class OutboxEventRepositoryImpl implements EventPublisherPort {

    private final OutboxEventMapper outboxEventMapper;
    private final ObjectMapper objectMapper;

    public OutboxEventRepositoryImpl(OutboxEventMapper outboxEventMapper, ObjectMapper objectMapper) {
        this.outboxEventMapper = outboxEventMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(List<DomainEvent> events) {
        for (DomainEvent event : events) {
            try {
                OutboxEventDO outboxEvent = new OutboxEventDO();
                outboxEvent.setId(System.currentTimeMillis());
                outboxEvent.setAggregateType(event.getClass().getSimpleName());
                
                // 从事件中提取聚合根ID（使用反射）
                try {
                    java.lang.reflect.Method getAdminId = event.getClass().getMethod("getAdminId");
                    Long aggregateId = (Long) getAdminId.invoke(event);
                    outboxEvent.setAggregateId(aggregateId);
                } catch (NoSuchMethodException e) {
                    outboxEvent.setAggregateId(0L);
                }
                
                outboxEvent.setEventType(event.getClass().getSimpleName());
                outboxEvent.setPayload(objectMapper.writeValueAsString(event));
                outboxEvent.setStatus(0); // 待投递
                outboxEvent.setRetryCount(0);
                outboxEvent.setVersion(0);
                outboxEvent.setCreatedAt(LocalDateTime.now());
                outboxEvent.setUpdatedAt(LocalDateTime.now());
                
                outboxEventMapper.insert(outboxEvent);
            } catch (Exception e) {
                throw new RuntimeException("Failed to persist domain event to outbox", e);
            }
        }
    }
}
