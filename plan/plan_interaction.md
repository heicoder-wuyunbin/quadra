# Quadra Interaction 服务实施计划

## 1. 技术栈选型
- JDK: Java 21
- 框架: Spring Boot 3.5.13
- ORM: MyBatis-Plus 3.5.15
- 数据库: MySQL 8 (库名 `quadra_interaction`)
- 架构: DDD 六边形架构 (Hexagonal)
- 配置: `.properties`

## 2. 领域建模映射
- Interaction 聚合根：围绕目标对象进行行为记录（LIKE/COMMENT）。
- 值对象：`TargetType`（MOVEMENT/VIDEO）、`ActionType`（LIKE/COMMENT）。
- 关键约束：LIKE 幂等、评论内容非空、取消为逻辑删除。
- OutboxEvent：互动产生或取消事件持久化。

## 3. 工程目录规划 (六边形多模块 Maven 标准)
```text
quadra-interaction (父工程)
├── pom.xml
│
├── quadra-interaction-domain
│   ├── pom.xml
│   └── src/main/java/com/quadra/interaction/domain
│       ├── model
│       ├── service
│       ├── event
│       └── exception
│
├── quadra-interaction-application
│   ├── pom.xml (依赖 domain)
│   └── src/main/java/com/quadra/interaction/application
│       ├── port.in
│       ├── port.out
│       └── service
│
├── quadra-interaction-adapter
│   ├── pom.xml
│   ├── quadra-interaction-adapter-in-web
│   │   ├── pom.xml (依赖 application, Spring Web)
│   │   └── src/main/java/com/quadra/interaction/adapter/in/web
│   └── quadra-interaction-adapter-out
│       ├── pom.xml (依赖 application, MyBatis-Plus)
│       └── src/main/java/com/quadra/interaction/adapter/out
│           ├── persistence
│           └── event
│
└── quadra-interaction-infrastructure
    ├── pom.xml (依赖所有子模块)
    ├── src/main/java/com/quadra/interaction/infrastructure
    │   ├── config
    │   └── QuadraInteractionApplication.java
    └── src/main/resources
        ├── application.properties
        └── mapper
```

### 模块依赖规则 (强约束)
- domain 无外部依赖。
- application 仅依赖 domain。
- adapter-in/out 仅依赖 application。
- infrastructure 依赖所有子模块。

## 4. 分层实施

### 4.1 Domain
- 聚合根：`Interaction`
- 值对象：
  - `TargetType`（MOVEMENT/VIDEO）
  - `ActionType`（LIKE/COMMENT）
- 约束：
  - LIKE 不允许重复有效记录
  - COMMENT 必须有内容
  - 取消点赞/删除评论为逻辑删除

### 4.2 Application
- UseCase：
  - `LikeTargetUseCase`
  - `CancelLikeUseCase`
  - `CommentTargetUseCase`
  - `ListCommentsQuery`
- Port.out：
  - `InteractionRepositoryPort`
  - `EventPublisherPort`

### 4.3 Adapter-out
- MyBatis-Plus 实现互动持久化
- Outbox 事件写入：
  - `InteractionCreatedEvent`
  - `InteractionCanceledEvent`
- 提供按 target 的评论分页查询

### 4.4 Adapter-in
- REST：
  - `POST /api/v1/interactions/likes`
  - `DELETE /api/v1/interactions/likes`
  - `POST /api/v1/interactions/comments`
  - `GET /api/v1/interactions/comments`

## 5. 核心难点与教学爆点处理方案
- 点赞幂等：基于唯一索引或幂等键保障单用户单目标单状态唯一。
- 评论分页：按 target+时间/ID 游标分页，避免深翻页性能劣化。
- 计数回写：产生/取消事件通过 MQ 供 content 端冗余计数更新，消费幂等。
- 热点写保护：按目标分片或引入限流，避免单热点导致抖动。

## 6. 里程碑
- M1：LIKE/COMMENT 写链路
- M2：评论分页查询
- M3：Outbox 投递与失败重试
- M4：联动 content 的计数更新

## 7. 验证标准
- 重复点赞可被正确拦截
- 评论分页结果正确稳定
- Outbox 事件可用于 content 计数回写
