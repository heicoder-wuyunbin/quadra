# Quadra (高学历相亲交友) 后端重构及数据库设计计划

## 1. 架构目标与设计原则
- **单库转微服务**：彻底摒弃 MySQL+MongoDB 混用，统一使用 MySQL 8。按微服务拆分独立数据库。
- **DDD 与六边形架构**：
  - **去物理外键**：跨域（甚至同域内不同聚合）通过业务 ID（如 Snowflake ID）弱关联。
  - **值对象 JSON 化**：利用 MySQL 8 的 JSON 特性存储无独立生命周期的值对象（如动态图片数组 medias、用户标签 tags 等）。
  - **聚合根设计**：每个表组围绕一个聚合根设计，支持乐观锁（`version`）和审计字段（`created_at`, `updated_at`, `deleted`）。
  - **空间索引**：利用 MySQL 8 的 `POINT` 和 `SPATIAL INDEX` 替代 MongoDB 的 `2dsphere` 来实现“附近的人”。

## 2. 限界上下文与微服务划分

根据业务功能，划分为以下 6 个核心微服务及其独立数据库：

### 2.1 用户服务 (`quadra_user`)
**职责**：用户账号生命周期、基础资料、个人偏好设置。
- `user` (聚合根)：账号、密码、环信账号、状态等。
- `user_profile` (实体)：昵称、头像、学历、收入等。标签 `tags` 使用 JSON 存储。
- `user_setting` (实体)：通知设置等。
- `stranger_question` (实体)：陌生人提问。
- `user_blacklist` (实体)：黑名单。

### 2.2 关系与LBS服务 (`quadra_relation`)
**职责**：用户间关系（好友、关注、喜欢）、地理位置、主页访客。
- `friendship` (聚合根)：双向好友关系。
- `user_follow` (聚合根)：单向关注（小视频等）。
- `user_match_like` (聚合根)：探探类互相喜欢记录（左滑右滑）。
- `user_visitor` (聚合根)：主页访客记录。
- `user_location` (聚合根/缓存模型)：当前地理位置。**采用 Redis GEO (GeoHash) 数据结构实现高频读写，应对“附近的人”秒级查询，MySQL 仅作为异步轨迹落库（可选）**。

### 2.3 内容服务 (`quadra_content`)
**职责**：图文动态（圈子）、短视频的发布与管理。
- `movement` (聚合根)：图文动态。`medias` 使用 JSON 数组存储，`location` 使用 `POINT`。
- `movement_timeline` (实体)：推模式下的时间线（收件箱）。
- `video` (聚合根)：短视频信息。

### 2.4 互动服务 (`quadra_interaction`)
**职责**：对动态和视频的评论、点赞行为（高频读写，独立拆分保护核心内容库）。
- `comment` (聚合根)：评论内容。
- `interaction_like` (聚合根)：点赞记录。

### 2.5 推荐域 (`quadra_recommend` - 引入 AI)
**职责**：三层架构驱动的现代化推荐系统，作为教学中的“AI 融合”亮点。
- **行为层 (燃料)**：`user_action_log` (聚合根)，记录用户行为（如 view/like/skip/dislike）及权重，取代无结构的普通日志。
- **特征层 (画像)**：`user_feature` / `content_feature` (聚合根)，存储用户与内容的标签、统计信息，以及接入本地大模型（如 Qwen / BGE）生成的 **Embedding 向量**。
- **结果层 (输出)**：基于规则推荐（同城优先、标签匹配）与 AI 向量相似度计算（Cosine Similarity），综合计算后落库到 `recommend_user` 与 `recommend_content` 供前端快速读取。

### 2.6 系统与数据服务 (`quadra_system`)
**职责**：后台管理、用户日志、数据分析。
- `admin_user` (聚合根)：管理员。
- `action_log` (实体)：用户行为日志。
- `data_analysis` (实体)：统计报表。

## 3. MongoDB 迁移至 MySQL 8 核心技术映射

| 原 MongoDB 特性/设计 | MySQL 8 替代方案 | 备注 |
| --- | --- | --- |
| `ObjectId` 主键 | 雪花算法 (Snowflake) `BIGINT` | 保证全局微服务下的唯一性与趋势递增 |
| `Array` (如 medias) | `JSON` 数据类型 | MySQL 8 支持原生 JSON 查询及索引，完美映射 DDD 值对象 |
| `2dsphere` 地理索引 | Redis `GEO` (GeoHash) | 应对高并发位置刷新与“附近的人”秒级查询，性能远超数据库原生空间索引 |
| 高并发自增 ID (`sequence`) | 分布式 ID / 雪花算法 | 微服务场景下直接使用分布式 ID 替代自增序列 |
| 弱结构化 Schema | `JSON` 列应对易变扩展字段 | 可选增加 `ext_data` JSON 列预留未来扩展能力 |

## 4. 六边形架构 (Hexagonal Architecture) 落地指南

每个微服务的代码结构统一遵循以下标准，保证核心领域逻辑不被框架污染：
```text
├── adapter (适配器层 - 对应六边形的边)
│   ├── in (入站：Web/REST Controller, MQ Listener, 定时任务)
│   └── out (出站：MySQL Repository 实现, Redis 客户端, Feign/RPC)
├── application (应用层)
│   ├── port (端口：in 用例接口, out 基础设施接口)
│   └── service (应用服务：编排领域模型，控制事务，Outbox投递)
├── domain (领域层 - 核心)
│   ├── model (聚合根, 实体, 值对象)
│   ├── service (领域服务)
│   ├── event (领域事件)
│   └── exception (领域异常)
└── infrastructure (基础设施层)
    └── config (Spring Boot 自动装配、MyBatis-Plus/JPA 配置等)
```

## 5. 领域事件与最终一致性
在微服务拆分后，跨域的交互不再通过硬编码或同步强依赖调用，而是通过领域事件（Domain Event）解耦：
- **领域内事件**：通过 Spring ApplicationEvent 在同服务内的不同聚合根之间同步状态。
- **跨服务事件**：通过**发件箱模式 (Outbox Pattern)** 结合消息队列 (如 RabbitMQ/RocketMQ)，保证微服务间的最终一致性。
  - *案例*：用户注册成功后发布 `UserRegisteredEvent`，推荐服务订阅后初始化用户的推荐池数据；消息服务订阅后发放新人福利。

## 6. 下一步计划
1. **等待确认**：审核本计划中的限界上下文划分及架构思路。
2. **DDL 编写**：计划通过后，将分服务编写 MySQL 8 的初始化建表脚本。
3. **工程搭建**：使用 Spring Boot 3 构建六边形多模块骨架。
4. **领域代码落地**：由内向外（Domain -> Application -> Adapter）实现业务。