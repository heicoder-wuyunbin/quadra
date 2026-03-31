请实现 MovementRepositoryPort 的 MyBatis-Plus 版本实现类。

要求：

1. 输入是 Domain 聚合根 Movement

2. 必须处理 medias JSON 字段的双向转换：
   - Domain 层：List<Media> medias
   - 数据库层：JSON string

3. 持久化到单表 movement：
   - MovementDO
   - 处理 longitude、latitude、location_name 等可选字段

4. 必须体现：
   - Repository != DAO
   - 是"聚合持久化器"

5. MovementInbox 扇出写入：
   - 调用 social 关注关系接口获取粉丝列表
   - 批量插入 movement_inbox 表
   - 每个粉丝一条记录（owner_id = 粉丝ID）

6. 禁止：
   - 在 Domain 层出现 DO
   - Controller 直接调用 Mapper

请给出：
- DO 类（MovementDO、MovementInboxDO）
- Mapper 接口
- RepositoryImpl 实现（含 JSON 转换逻辑）
