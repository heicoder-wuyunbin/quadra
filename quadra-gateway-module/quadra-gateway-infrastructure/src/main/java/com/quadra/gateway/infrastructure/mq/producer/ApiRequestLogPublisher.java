package com.quadra.gateway.infrastructure.mq.producer;

import com.quadra.gateway.infrastructure.mq.config.RocketMQTopicConfig;
import com.quadra.gateway.infrastructure.mq.message.ApiRequestLogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

/**
 * 接口请求日志 MQ 发布器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiRequestLogPublisher {

    private final RocketMQTemplate rocketMQTemplate;
    private final RocketMQTopicConfig topicConfig;

    public void publish(ApiRequestLogMessage message) {
        String topic = topicConfig.getRequestLog();
        if (topic == null || topic.isBlank()) {
            return;
        }
        try {
            rocketMQTemplate.convertAndSend(topic, message);
        } catch (Exception e) {
            // 日志投递失败不影响主流程
            log.warn("发送接口请求日志到 MQ 失败：topic={}, traceId={}, path={}", topic, message.getTraceId(), message.getPath(), e);
        }
    }
}

