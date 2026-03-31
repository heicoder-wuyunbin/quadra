请实现 ActionRepositoryPort 的 MyBatis-Plus 版本实现类。

要求：

1. 输入是 Domain 聚合根 UserActionLog
2. 必须支持：
   - 行为日志批量写入（batch insert）
   - 特征表 upsert（按用户/内容维度更新）
   - AI embedding JSON 字段处理（预留扩展）

3. 在一个事务内：
   - 批量插入行为日志
   - 更新/插入用户特征画像
   - 更新/插入内容特征画像

4. 必须体现：
   - Repository != DAO
   - 是"聚合持久化器"
   - 支持 upsert 语义（INSERT ON DUPLICATE KEY UPDATE）

5. 禁止：
   - 在 Domain 层出现 DO
   - Controller 直接调用 Mapper

请给出：
- DO 类（UserActionLogDO, UserFeatureDO, ContentFeatureDO）
- Mapper 接口
- RepositoryImpl 实现
