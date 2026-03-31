请实现 Outbox 模式中的事件写入逻辑。

要求：

1. 在事务内写入 outbox_event 表
2. 字段包含：
   - aggregate_type（改为 UserFeature / RecommendUser / RecommendContent）
   - aggregate_id
   - event_type（如 USER_FEATURE_BUILT / RECOMMEND_RESULT_REFRESHED）
   - payload
   - status
   - retry_count
   - next_retry_time

3. payload 使用 JSON 存储

4. 适配事件类型：
   - UserFeatureBuiltEvent：用户画像构建完成
   - RecommendResultRefreshedEvent：推荐结果刷新完成

5. 提供：
   - OutboxEventDO
   - Mapper
   - Repository
   - EventPublisherPort 实现

6. 禁止直接发送 MQ

请解释：
- 为什么必须"事务内写入"
- 画像构建/结果刷新事件的业务意义
