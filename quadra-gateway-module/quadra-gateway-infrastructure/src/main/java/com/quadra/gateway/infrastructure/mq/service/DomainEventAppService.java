package com.quadra.gateway.infrastructure.mq.service;

import com.quadra.gateway.infrastructure.mq.event.AdminLoginEventPayload;
import com.quadra.gateway.infrastructure.mq.producer.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 领域事件应用服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DomainEventAppService {
    
    private final DomainEventPublisher<AdminLoginEventPayload> domainEventPublisher;
    
    /**
     * 发送管理员登录事件
     *
     * @param payload 事件载荷
     */
    public void sendAdminLoginEvent(AdminLoginEventPayload payload) {
        domainEventPublisher.publish(
            "TOPIC_ADMIN_EVENT",
            "AdminLoginEvent",
            "Admin",
            payload.getAdminId(),
            payload
        );
    }
}
