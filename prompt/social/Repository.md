请实现 FollowRepositoryPort 的 MyBatis-Plus 版本实现类。

要求：

1. 输入是 Domain 聚合根 UserFollow

2. 单表持久化（不像 user 模块拆三表）：
   - UserFollowDO

3. 在一个事务内：
   - 调用 Mapper 插入数据库
   - 处理 DuplicateKeyException 兜底（防止重复关注）

4. 双向好友场景（FriendshipRepositoryImpl）：
   - 同事务内插入两条记录（A→B 与 B→A）
   - 保证双向关系一致性

5. 必须体现：
   - Repository != DAO
   - 是"聚合持久化器"

6. 禁止：
   - 在 Domain 层出现 DO
   - Controller 直接调用 Mapper

请给出：
- DO 类
- Mapper 接口
- RepositoryImpl 实现
