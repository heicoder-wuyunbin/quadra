请实现 Outbox 模式中的事件写入逻辑。

要求：

1. 在事务内写入 outbox_event 表
2. 字段包含：
   - aggregate_type
   - aggregate_id
   - event_type
   - payload
   - status
   - retry_count
   - next_retry_time

3. payload 使用 JSON 存储

4. 提供：
   - OutboxEventDO
   - Mapper
   - Repository
   - EventPublisherPort 实现

5. 禁止直接发送 MQ

请解释：
- 为什么必须“事务内写入”