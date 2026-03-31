package com.quadra.user.adapter.out.event;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quadra.user.adapter.out.persistence.entity.OutboxEventDO;
import com.quadra.user.adapter.out.persistence.mapper.OutboxEventMapper;
import com.quadra.user.application.port.out.EventPublisherPort;
import com.quadra.user.domain.event.DomainEvent;
import com.quadra.user.domain.event.UserRegisteredEvent;
import org.springframework.stereotype.Component;

import java.util.List;

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
            
            if (event instanceof UserRegisteredEvent) {
                UserRegisteredEvent ure = (UserRegisteredEvent) event;
                eventDO.setAggregateType("User");
                eventDO.setAggregateId(ure.getUserId());
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
