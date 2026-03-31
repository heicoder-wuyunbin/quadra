# RocketMQ 领域事件集成指南

## 概述

本项目使用 RocketMQ 作为领域事件的传输中间件，实现微服务之间的解耦和事件的可靠投递。

## 架构设计

### 组件说明

1. **DomainEventPublisher** - 领域事件发布器
   - 泛型设计，支持任意事件载荷
   - 自动构建事件元数据（eventId, timestamp 等）
   - 提供 `publish()` 和 `publishSimple()` 两种方法

2. **DomainEventMessage** - 事件消息结构
   - `eventId`: 事件唯一标识（UUID）
   - `eventType`: 事件类型
   - `aggregateType`: 聚合根类型
   - `aggregateId`: 聚合根 ID
   - `payload`: 事件载荷
   - `timestamp`: 事件发生时间

3. **Consumer** - 事件消费者
   - 监听指定 Topic
   - 处理接收到的事件
   - 实现业务逻辑

## 使用示例

### 1. 发送领域事件

```java
@Service
@RequiredArgsConstructor
public class AdminService {
    
    private final DomainEventPublisher<AdminLoginEventPayload> eventPublisher;
    
    public AdminLoginResultDTO login(AdminLoginRequest request) {
        // 1. 执行登录逻辑
        AdminLoginResultDTO result = authenticate(request);
        
        // 2. 发送领域事件
        AdminLoginEventPayload payload = AdminLoginEventPayload.builder()
                .adminId(result.getAdminId())
                .username(result.getUsername())
                .realName(result.getRealName())
                .loginIp(getClientIp())
                .build();
        
        eventPublisher.publish(
            "TOPIC_ADMIN_EVENT",
            "AdminLoginEvent",
            "Admin",
            result.getAdminId(),
            payload
        );
        
        return result;
    }
}
```

### 简化版发送

```java
// 不需要聚合根信息时使用
eventPublisher.publishSimple(
    "TOPIC_USER_EVENT",
    "UserRegisteredEvent",
    payload
);
```

### 2. 消费领域事件

```java
@Component
@RocketMQMessageListener(
    topic = "TOPIC_ADMIN_EVENT",
    consumerGroup = "consumer-admin-event-group"
)
public class AdminEventConsumer implements RocketMQListener<DomainEventPublisher.DomainEventMessage<AdminLoginEventPayload>> {
    
    @Override
    public void onMessage(DomainEventPublisher.DomainEventMessage<AdminLoginEventPayload> message) {
        log.info("收到管理员事件：type={}, adminId={}", 
                message.getEventType(), 
                message.getPayload().getAdminId());
        
        // 处理事件
        processAdminLogin(message.getPayload());
    }
    
    private void processAdminLogin(AdminLoginEventPayload payload) {
        // 业务逻辑处理
    }
}
```

### 3. 自定义事件类型

#### 创建事件载荷

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomEventPayload implements Serializable {
    private Long userId;
    private String action;
    private LocalDateTime timestamp;
}
```

#### 发送自定义事件

```java
@Service
public class CustomService {
    
    @Autowired
    private DomainEventPublisher<CustomEventPayload> eventPublisher;
    
    public void doSomething() {
        CustomEventPayload payload = CustomEventPayload.builder()
                .userId(userId)
                .action("CREATE")
                .timestamp(LocalDateTime.now())
                .build();
        
        eventPublisher.publish(
            "TOPIC_CUSTOM_EVENT",
            "CustomEventCreated",
            "Custom",
            userId,
            payload
        );
    }
}
```

## Topic 规划

| Topic 名称 | 说明 | 生产者 | 消费者组示例 |
|-----------|------|--------|-------------|
| TOPIC_ADMIN_EVENT | 管理员相关事件 | Gateway | consumer-admin-event-group |
| TOPIC_USER_ACTION | 用户行为事件 | Gateway | consumer-user-action-group |
| TOPIC_CONTENT_EVENT | 内容相关事件 | Content 服务 | consumer-content-event-group |
| TOPIC_SOCIAL_EVENT | 社交相关事件 | Social 服务 | consumer-social-event-group |

## 配置说明

### application.properties

```properties
# RocketMQ 基础配置
rocketmq.name-server=localhost:9876
rocketmq.producer.group=quadra-producer-group

# 生产者配置（可选）
rocketmq.producer.send-message-timeout=3000
rocketmq.producer.retry-times-when-send-failed=2
rocketmq.producer.retry-times-when-send-async-failed=2

# Topic 配置（可选）
rocketmq.topic.admin-event=TOPIC_ADMIN_EVENT
rocketmq.topic.user-action=TOPIC_USER_ACTION
```

### pom.xml

```xml
<dependency>
    <groupId>org.apache.rocketmq</groupId>
    <artifactId>rocketmq-spring-boot-starter</artifactId>
    <version>2.3.5</version>
</dependency>
```

### Docker Compose

确保 RocketMQ 服务已启动：

```bash
cd infra
docker-compose up -d rocketmq-namesrv rocketmq-broker rocketmq-dashboard
```

访问 RocketMQ Dashboard: http://localhost:8081

## 最佳实践

### 1. 事件命名规范

- 使用过去时态：`AdminLoginEvent`, `UserCreatedEvent`
- 体现业务含义：`QuestionDisabledEvent`, `ContentApprovedEvent`
- 格式：`{聚合根}{动作}Event`

### 2. 载荷设计

- 只包含必要字段
- 避免传递大对象
- 使用基本类型和字符串
- 实现 `Serializable` 接口

### 3. 幂等性处理

消费者需要实现幂等性，避免重复消费：

```java
@Component
public class IdempotentConsumer {
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Override
    public void onMessage(DomainEventPublisher.DomainEventMessage<?> event) {
        String key = "event_processed:" + event.getEventId();
        
        // 检查是否已处理
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            log.warn("事件已处理，跳过：{}", event.getEventId());
            return;
        }
        
        // 处理事件
        processEvent(event);
        
        // 标记已处理
        redisTemplate.opsForValue().set(key, "1", 24, TimeUnit.HOURS);
    }
}
```

### 4. 异常处理

- 消费者抛出异常会触发 RocketMQ 重试机制
- 可以设置最大重试次数
- 失败的消息会进入死信队列

```java
@Component
@RocketMQMessageListener(
    topic = "TOPIC_ADMIN_EVENT",
    consumerGroup = "consumer-admin-event-group",
    maxReconsumeTimes = 3  // 最多重试 3 次
)
public class AdminEventConsumer implements RocketMQListener<T> {
    
    @Override
    public void onMessage(T message) {
        try {
            processEvent(message);
        } catch (BusinessException e) {
            // 业务异常，记录但不重试
            log.error("业务异常：{}", e.getMessage());
        } catch (Exception e) {
            // 系统异常，抛出以触发重试
            log.error("系统异常，将重试", e);
            throw e;
        }
    }
}
```

## 监控与运维

### 1. 查看消息堆积

通过 RocketMQ Dashboard 查看：
1. 访问 http://localhost:8081
2. 点击 "Topics" 标签
3. 选择对应的 Topic
4. 查看消息堆积情况

### 2. 查看消费者状态

1. 点击 "Consumers" 标签
2. 选择消费者组
3. 查看消费 TPS、延迟等指标

### 3. 日志监控

关键日志：
- 发送成功：`发送领域事件成功：topic=xxx, eventId=xxx`
- 发送失败：`发送领域事件失败：topic=xxx`
- 接收成功：`收到 xxx 事件：type=xxx, id=xxx`
- 处理异常：记录完整堆栈

### 4. 告警配置

建议配置以下告警：
- 消息堆积超过阈值（如 1000 条）
- 消费者连续失败超过阈值
- 生产者发送失败

## 故障排查

### 常见问题

#### 1. 消息发送失败

**现象**: 日志显示 "发送领域事件失败"

**排查步骤**:
1. 检查 RocketMQ 是否启动：`docker-compose ps`
2. 检查 NameServer 地址：`rocketmq.name-server=localhost:9876`
3. 检查 Topic 是否已创建（在 Dashboard 查看）
4. 检查网络连接

#### 2. 消息未消费

**现象**: 发送成功但消费者没收到

**排查步骤**:
1. 检查消费者组是否正确
2. 检查 `@RocketMQMessageListener` 的 topic 配置
3. 查看消费者日志是否有异常
4. 在 Dashboard 查看消息堆积情况

#### 3. 重复消费

**现象**: 同一条消息被多次处理

**解决方案**:
- 实现幂等性（使用 Redis 记录已处理的事件 ID）
- 参考最佳实践中的幂等性处理示例

#### 4. 消息堆积

**现象**: Dashboard 显示消息堆积量持续增长

**排查步骤**:
1. 检查消费者是否正常启动
2. 检查消费者处理逻辑是否过慢
3. 检查是否有异常导致消费失败
4. 考虑增加消费者实例

## 性能优化

### 1. 批量消费

如果业务允许，可以批量处理消息：

```java
@Component
@RocketMQMessageListener(
    topic = "TOPIC_USER_EVENT",
    consumerGroup = "consumer-user-event-group",
    consumeThreadMax = 20  // 增加消费线程数
)
public class UserEventConsumer implements RocketMQListener<T> {
    // ...
}
```

### 2. 异步处理

在消费者中异步处理耗时操作：

```java
@Override
public void onMessage(T message) {
    // 快速返回，异步处理
    CompletableFuture.runAsync(() -> {
        processEvent(message);
    });
}
```

### 3. 分区顺序

如果需要保证同一聚合根的消息顺序：

```java
@RocketMQMessageListener(
    topic = "TOPIC_USER_EVENT",
    consumerGroup = "consumer-user-event-group",
    selectorExpression = "userId == 123"  // 消息队列选择器
)
```

## 参考资料

- [RocketMQ 官方文档](https://rocketmq.apache.org/docs/)
- [RocketMQ Spring Boot Starter](https://github.com/apache/rocketmq-spring)
- [领域驱动设计](https://martinfowler.com/tags/domain_driven_design.html)
- [事件驱动架构](https://www.enterpriseintegrationpatterns.com/patterns/messaging/EventDrivenConsumer.html)
