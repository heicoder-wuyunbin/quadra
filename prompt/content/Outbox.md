请实现 Outbox 模式中的事件写入逻辑。

要求：

1. 在事务内写入 outbox_event 表

2. 字段包含：
   - aggregate_type（Movement / Video）
   - aggregate_id
   - event_type（MovementPublishedEvent / VideoPublishedEvent）
   - payload
   - status
   - retry_count
   - next_retry_time

3. payload 使用 JSON 存储，包含：
   - movementId / videoId
   - userId
   - createdAt

4. 提供：
   - OutboxEventDO
   - Mapper
   - Repository
   - EventPublisherPort 实现

5. 禁止直接发送 MQ

请解释：
- 为什么必须"事务内写入"
- Content 域事件如何被下游消费（如 interaction 服务订阅更新计数）
