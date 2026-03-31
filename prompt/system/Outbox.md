请实现 Outbox 模式中的事件写入逻辑。

要求：

1. 在事务内写入 outbox_event 表
2. 字段包含：
   - aggregate_type（聚合根类型：SysAdmin / SysRole）
   - aggregate_id
   - event_type（AdminCreatedEvent / RoleChangedEvent）
   - payload
   - status
   - retry_count
   - next_retry_time

3. payload 使用 JSON 存储

4. 适配的管理员创建、角色变更事件：
   - AdminCreatedEvent：管理员创建时触发
   - RoleAssignedEvent：角色分配给管理员时触发
   - MenuGrantedEvent：菜单权限授予角色时触发

5. 提供：
   - OutboxEventDO
   - Mapper
   - Repository
   - EventPublisherPort 实现

6. 禁止直接发送 MQ

7. 审计日志与事件一致性：
   - 管理员操作日志必须在同一事务内写入
   - 确保业务操作与日志记录的原子性

请解释：
- 为什么必须"事务内写入"
- 如何保证审计日志与业务操作的一致性
