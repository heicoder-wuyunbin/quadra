请实现 AdminRepositoryPort 的 MyBatis-Plus 版本实现类。

要求：

1. 输入是 Domain 聚合根 SysAdmin
2. 必须拆分为：
   - SysAdminDO
   - SysAdminRoleDO（管理员-角色关联表）

3. 在一个事务内：
   - 分别调用 2 个 Mapper 插入数据库
   - 使用同一个 adminId
   - 处理角色关联表的批量插入

4. 角色权限聚合持久化：
   - SysRoleDO + SysRoleMenuDO（角色-菜单权限关联表）
   - 保证角色授权幂等

5. 必须体现：
   - Repository != DAO
   - 是"聚合持久化器"
   - 管理员状态变更时的乐观锁处理

6. 禁止：
   - 在 Domain 层出现 DO
   - Controller 直接调用 Mapper

请给出：
- DO 类
- Mapper 接口
- RepositoryImpl 实现
