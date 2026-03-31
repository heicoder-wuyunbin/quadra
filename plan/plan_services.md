# Quadra 多微服务实施计划总索引

## 1. 计划文档拆分
- `quadra_social`：见 `plan_social.md`
- `quadra_content`：见 `plan_content.md`
- `quadra_interaction`：见 `plan_interaction.md`
- `quadra_recommend`：见 `plan_recommend.md`
- `quadra_system`：见 `plan_system.md`

## 2. 建议实施顺序
1. `quadra_social`
2. `quadra_content`
3. `quadra_interaction`
4. `quadra_recommend`
5. `quadra_system`

## 3. 通用技术约束
- adapter-in 仅依赖 application，禁止直接依赖 adapter-out。
- Domain 禁止 Spring/MyBatis 注解。
- 所有跨服务传播通过 Outbox + MQ，不做跨库事务。
- 查询接口统一分页模型，写接口统一 `Result` 包装。
