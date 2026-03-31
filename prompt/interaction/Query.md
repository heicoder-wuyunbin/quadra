请实现评论列表查询功能（CQRS）。

要求：

1. 必须走 InteractionQueryPort / InteractionQueryService

2. 不允许返回 Domain 对象

3. 返回 DTO（CommentDTO）：
   - 包含评论ID、用户ID、目标ID、内容、回复对象ID、创建时间等

4. 分页策略：
   - 按 target_id + target_type + created_at 游标分页
   - 避免深翻页性能劣化

5. 可以直接使用 MyBatis 查询（允许绕过聚合）

6. 输出：
   - InteractionQueryPort
   - InteractionQueryService
   - CommentDTO
   - Mapper 查询方法

强调：
- Query 是"读模型"，不是 Domain 行为
- 评论列表只返回 action_type=COMMENT 且 deleted=0 的记录
