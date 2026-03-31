请实现 UserRepositoryPort 的 MyBatis-Plus 版本实现类。

要求：

1. 输入是 Domain 聚合根 User
2. 必须拆分为：
   - UserDO
   - UserProfileDO
   - UserSettingDO

3. 在一个事务内：
   - 分别调用 3 个 Mapper 插入数据库
   - 使用同一个 userId

4. 必须体现：
   - Repository != DAO
   - 是“聚合持久化器”

5. 禁止：
   - 在 Domain 层出现 DO
   - Controller 直接调用 Mapper

请给出：
- DO 类
- Mapper 接口
- RepositoryImpl 实现