package com.quadra.gateway.infrastructure.mq.consumer;

import com.quadra.gateway.infrastructure.mq.event.UserActionEventPayload;
import com.quadra.gateway.infrastructure.mq.producer.DomainEventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 用户行为事件消费者
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "TOPIC_USER_ACTION",
    consumerGroup = "consumer-user-action-group"
)
public class UserActionEventConsumer implements RocketMQListener<DomainEventPublisher.DomainEventMessage<UserActionEventPayload>> {
    
    @Override
    public void onMessage(DomainEventPublisher.DomainEventMessage<UserActionEventPayload> message) {
        log.info("收到用户行为事件：eventId={}, eventType={}, userId={}, actionType={}", 
                message.getEventId(), 
                message.getEventType(),
                message.getPayload() != null ? message.getPayload().getUserId() : null,
                message.getPayload() != null ? message.getPayload().getActionType() : null);
        
        // TODO: 处理用户行为事件，例如更新推荐系统
        if (message.getPayload() != null) {
            processUserAction(message.getPayload());
        }
    }
    
    private void processUserAction(UserActionEventPayload payload) {
        // 业务逻辑处理
        log.info("处理用户行为：userId={}, targetId={}, actionType={}", 
                payload.getUserId(), payload.getTargetId(), payload.getActionType());
    }
}
