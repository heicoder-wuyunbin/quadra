请实现关注关系查询功能（CQRS）。

要求：

1. 必须走 QueryPort / QueryService
2. 不允许返回 Domain 对象
3. 返回 DTO（FollowerDTO）

4. 可以直接使用 MyBatis 查询（允许绕过聚合）

5. 输出：
   - FollowQueryPort
   - SocialQueryService
   - FollowerDTO / FollowingDTO
   - Mapper 查询方法

6. 分页查询优化：
   - 支持分页参数
   - 合理使用索引（idx_target_user_id 查粉丝，uk_user_target 查关注）

强调：
- Query 是"读模型"，不是 Domain 行为
- 高频查询可同步至 Redis Set 加速
