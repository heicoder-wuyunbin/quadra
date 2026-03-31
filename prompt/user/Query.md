请实现用户资料查询功能（CQRS）。

要求：

1. 必须走 QueryPort / QueryService
2. 不允许返回 Domain 对象
3. 返回 DTO（UserProfileDTO）

4. 可以直接使用 MyBatis 查询（允许绕过聚合）

5. 输出：
   - UserQueryPort
   - UserQueryService
   - DTO
   - Mapper 查询方法

强调：
- Query 是“读模型”，不是 Domain 行为