package com.quadra.gateway.infrastructure.mq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * RocketMQ 配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "rocketmq.topic")
public class RocketMQTopicConfig {
    
    /**
     * 管理员事件 Topic
     */
    private String adminEvent;
    
    /**
     * 用户行为事件 Topic
     */
    private String userAction;
    
    /**
     * 内容事件 Topic
     */
    private String contentEvent;
    
    /**
     * 社交事件 Topic
     */
    private String socialEvent;
}
