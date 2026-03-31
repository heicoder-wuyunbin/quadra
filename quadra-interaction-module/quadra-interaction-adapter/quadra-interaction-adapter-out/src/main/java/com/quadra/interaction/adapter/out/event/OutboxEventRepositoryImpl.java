package com.quadra.interaction.adapter.out.event;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.interaction.adapter.out.persistence.entity.OutboxEventDO;
import com.quadra.interaction.adapter.out.persistence.mapper.OutboxEventMapper;
import com.quadra.interaction.application.port.out.EventPublisherPort;
import com.quadra.interaction.domain.event.DomainEvent;
import com.quadra.interaction.domain.event.InteractionCreatedEvent;
import com.quadra.interaction.domain.event.InteractionCanceledEvent;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Outbox 事件仓储实现
 * 将领域事件写入 outbox_event 表，保证与业务操作在同一个本地事务中
 */
@Component
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
            OutboxEventDO eventDO = new OutboxEventDO();
            eventDO.setId(IdWorker.getId());
            eventDO.setEventType(event.getClass().getSimpleName());
            
            if (event instanceof InteractionCreatedEvent) {
                InteractionCreatedEvent ice = (InteractionCreatedEvent) event;
                eventDO.setAggregateType("Interaction");
                eventDO.setAggregateId(ice.getInteractionId());
            } else if (event instanceof InteractionCanceledEvent) {
                InteractionCanceledEvent ice = (InteractionCanceledEvent) event;
                eventDO.setAggregateType("Interaction");
                eventDO.setAggregateId(ice.getInteractionId());
            } else {
                eventDO.setAggregateType("Unknown");
                eventDO.setAggregateId(0L);
            }
            
            try {
                eventDO.setPayload(objectMapper.writeValueAsString(event));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("序列化领域事件失败", e);
            }
            
            eventDO.setStatus(0); // 0-待投递
            eventDO.setRetryCount(0);
            
            outboxEventMapper.insert(eventDO);
        }
    }
}
