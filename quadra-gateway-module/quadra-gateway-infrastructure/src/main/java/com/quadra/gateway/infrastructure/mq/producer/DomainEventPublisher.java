package com.quadra.gateway.infrastructure.mq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 通用领域事件发布器
 * 
 * @param <T> 事件载荷类型
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DomainEventPublisher<T> {
    
    private final RocketMQTemplate rocketMQTemplate;
    
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 发送领域事件
     *
     * @param topic 主题
     * @param eventType 事件类型
     * @param aggregateType 聚合根类型
     * @param aggregateId 聚合根 ID
     * @param payload 事件载荷
     */
    public void publish(String topic, String eventType, String aggregateType, Long aggregateId, T payload) {
        DomainEventMessage<T> message = DomainEventMessage.<T>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .payload(payload)
                .timestamp(LocalDateTime.now().format(DATETIME_FORMATTER))
                .build();
        
        try {
            Message<DomainEventMessage<T>> mqMessage = MessageBuilder.withPayload(message).build();
            rocketMQTemplate.send(topic, mqMessage);
            log.info("发送领域事件成功：topic={}, eventId={}, eventType={}, aggregateId={}", 
                    topic, message.getEventId(), message.getEventType(), message.getAggregateId());
        } catch (Exception e) {
            log.error("发送领域事件失败：topic={}, eventId={}, eventType={}", 
                    topic, message.getEventId(), message.getEventType(), e);
            throw new RuntimeException("发送领域事件失败", e);
        }
    }
    
    /**
     * 发送领域事件（简化版，不需要聚合根信息）
     *
     * @param topic 主题
     * @param eventType 事件类型
     * @param payload 事件载荷
     */
    public void publishSimple(String topic, String eventType, T payload) {
        publish(topic, eventType, "Unknown", null, payload);
    }
    
    /**
     * 领域事件消息
     */
    @lombok.Builder
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DomainEventMessage<T> implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        
        /**
         * 事件 ID
         */
        private String eventId;
        
        /**
         * 事件类型
         */
        private String eventType;
        
        /**
         * 聚合根类型
         */
        private String aggregateType;
        
        /**
         * 聚合根 ID
         */
        private Long aggregateId;
        
        /**
         * 事件载荷
         */
        private T payload;
        
        /**
         * 事件时间戳
         */
        private String timestamp;
    }
}
