请实现推荐结果查询功能（CQRS）。

要求：

1. 必须走 QueryPort / QueryService
2. 不允许返回 Domain 对象
3. 返回 DTO（RecommendUserDTO）

4. 可以直接使用 MyBatis 查询（允许绕过聚合）

5. 查询要求：
   - 按 user_id 筛选
   - 按 recommend_date 筛选（当日推荐）
   - 按 score 降序排序
   - 支持分页

6. 输出：
   - RecommendQueryPort
   - RecommendQueryService
   - RecommendUserDTO
   - Mapper 查询方法

强调：
- Query 是"读模型"，不是 Domain 行为
- 推荐结果表是预计算缓存，直接读取即可
