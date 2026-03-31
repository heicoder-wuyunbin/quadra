请实现 InteractionRepositoryPort 的 MyBatis-Plus 版本实现类。

要求：

1. 输入是 Domain 聚合根 Interaction

2. 单表聚合持久化（interaction 表统一存储 LIKE/COMMENT）：
   - InteractionDO 包含：id, user_id, target_id, target_type, action_type, content, reply_to_id, deleted 等字段

3. 关键行为：
   - save()：插入新互动记录
   - 点赞幂等：通过唯一索引 uk_like_limit 拦截重复点赞，捕获 DuplicateKeyException 返回已存在标记
   - 取消点赞/删除评论：逻辑删除（更新 deleted=1），不物理删除

4. 必须体现：
   - Repository != DAO
   - 是"聚合持久化器"

5. 禁止：
   - 在 Domain 层出现 DO
   - Controller 直接调用 Mapper
   - LIKE/COMMENT 拆成两张表

请给出：
- DO 类
- Mapper 接口
- RepositoryImpl 实现（含幂等处理逻辑）
