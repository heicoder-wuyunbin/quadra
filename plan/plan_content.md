# Quadra Content 服务实施计划

## 1. 技术栈选型
- JDK: Java 21
- 框架: Spring Boot 3.5.13
- ORM: MyBatis-Plus 3.5.15
- 数据库: MySQL 8 (库名 `quadra_content`)
- 架构: DDD 六边形架构 (Hexagonal)
- 配置: `.properties`

## 2. 领域建模映射
- Movement 聚合根：图文动态。保障内容完整性、状态机合法（草稿/发布/删除）。
- Video 聚合根：短视频。支持元数据与转码状态占位。
- MovementInboxItem 实体：时间线收件箱项，由发布时扇出写入。
- OutboxEvent：内容发布/删除事件持久化。

## 3. 工程目录规划 (六边形多模块 Maven 标准)
```text
quadra-content (父工程)
├── pom.xml
│
├── quadra-content-domain
│   ├── pom.xml
│   └── src/main/java/com/quadra/content/domain
│       ├── model
│       ├── service
│       ├── event
│       └── exception
│
├── quadra-content-application
│   ├── pom.xml (依赖 domain)
│   └── src/main/java/com/quadra/content/application
│       ├── port.in
│       ├── port.out
│       └── service
│
├── quadra-content-adapter
│   ├── pom.xml
│   ├── quadra-content-adapter-in-web
│   │   ├── pom.xml (依赖 application, Spring Web)
│   │   └── src/main/java/com/quadra/content/adapter/in/web
│   ├── quadra-content-adapter-in-mq
│   │   ├── pom.xml (依赖 application, MQ Client)
│   │   └── src/main/java/com/quadra/content/adapter/in/mq
│   └── quadra-content-adapter-out
│       ├── pom.xml (依赖 application, MyBatis-Plus, Redis)
│       └── src/main/java/com/quadra/content/adapter/out
│           ├── persistence
│           ├── cache
│           └── event
│
└── quadra-content-infrastructure
    ├── pom.xml (依赖所有子模块)
    ├── src/main/java/com/quadra/content/infrastructure
    │   ├── config
    │   └── QuadraContentApplication.java
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
- 聚合根：`Movement`、`Video`
- 实体：`MovementInboxItem`
- 约束：
  - 内容不能为空（文本或媒体至少一项）
  - 审核状态机合法流转
  - 删除仅逻辑删除

### 4.2 Application
- UseCase：
  - `PublishMovementUseCase`
  - `PublishVideoUseCase`
  - `DeleteMovementUseCase`
  - `PullMyTimelineQuery`
- Port.out：
  - `MovementRepositoryPort`
  - `VideoRepositoryPort`
  - `TimelineRepositoryPort`
  - `EventPublisherPort`

### 4.3 Adapter-out
- MyBatis-Plus 持久化
- Inbox 推模式写入
- Outbox 事件写入：
  - `MovementPublishedEvent`
  - `VideoPublishedEvent`

### 4.4 Adapter-in
- REST：
  - `POST /api/v1/content/movements`
  - `POST /api/v1/content/videos`
  - `DELETE /api/v1/content/movements/{id}`
  - `GET /api/v1/content/timeline`
- MQ Listener（后续）：
  - 消费 interaction 计数事件，更新冗余 like/comment 计数

## 5. 核心难点与教学爆点处理方案
- 发布校验与状态机：命令对象校验 + 聚合内守护不变量，避免脏数据。
- 时间线扇出策略：调用 social 关注集进行粉丝收件箱写入，失败落重试队列。
- 媒体处理占位：视频转码/封面生成以异步事件衔接，主链路不阻塞。
- 冗余计数回写：依赖 interaction 域事件，采用幂等更新策略。

## 6. 里程碑
- M1：骨架 + 发布接口
- M2：Timeline 查询 + 分页
- M3：Outbox 投递闭环
- M4：联动 social 的关注关系进行 Inbox 扇出

## 7. 验证标准
- 构建通过，服务可启动
- 发布动态后可在本人时间线读取
- Outbox 事件生成与重试状态正确
