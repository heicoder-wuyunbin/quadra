请实现 Outbox 模式中的事件写入逻辑。

要求：

1. 在事务内写入 outbox_event 表

2. 字段包含：
   - aggregate_type（固定为 "Interaction"）
   - aggregate_id（interaction 表主键）
   - event_type（InteractionCreatedEvent / InteractionCanceledEvent）
   - payload（JSON 格式，包含 userId, targetId, targetType, actionType 等）
   - status
   - retry_count
   - next_retry_time

3. payload 使用 JSON 存储，供 content 域消费：
   - content 域收到事件后更新冗余计数（如 movement.like_count）
   - 消费端需保证幂等

4. 提供：
   - OutboxEventDO
   - Mapper
   - Repository
   - EventPublisherPort 实现

5. 禁止直接发送 MQ

请解释：
- 为什么必须"事务内写入"
- 为什么计数更新要通过事件跨域，而不是直接 SQL 更新 content 表
