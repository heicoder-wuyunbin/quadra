请实现权限树查询功能（CQRS）。

要求：

1. 必须走 MenuQueryPort / SystemQueryService
2. 不允许返回 Domain 对象
3. 返回 DTO（MenuTreeDTO / PermissionDTO）

4. 可以直接使用 MyBatis 查询（允许绕过聚合）

5. RBAC 权限读取路径缓存优化：
   - 查询管理员的所有角色
   - 查询角色的所有菜单权限
   - 构建树形菜单结构
   - 缓存权限列表，避免每次请求都查询数据库

6. 避免 N+1 查询：
   - 使用 IN 查询一次性获取所有角色
   - 使用 JOIN 或 IN 查询获取所有菜单权限

7. 输出：
   - MenuQueryPort
   - SystemQueryService
   - DTO（MenuTreeDTO、PermissionDTO、AdminPermissionDTO）
   - Mapper 查询方法

强调：
- Query 是"读模型"，不是 Domain 行为
- 权限数据适合缓存，减少数据库压力
