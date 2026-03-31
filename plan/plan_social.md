# Quadra Social 服务实施计划

## 1. 技术栈选型
- JDK: Java 21
- 框架: Spring Boot 3.5.13
- ORM: MyBatis-Plus 3.5.15
- 数据库: MySQL 8 (库名 `quadra_social`)
- 架构: DDD 六边形架构 (Hexagonal)
- 配置: `.properties`

## 2. 领域建模映射
- Friendship 聚合根：双向好友关系。新增好友需写入 A→B 与 B→A 两条关系以便快速查询。
- UserFollow 聚合根：单向关注关系。高频写读，支持 Redis Set 加速。
- UserMatchLike 聚合根：滑动匹配（LIKE/DISLIKE），当双方 LIKE 形成匹配事件。
- UserVisitor 聚合根：访客记录，按时间排序分页。
- UserLocationTrack 实体：用户位置轨迹（可选增强，使用 Redis GEO 做附近的人）。
- OutboxEvent：领域事件持久化载体。

## 3. 工程目录规划 (六边形多模块 Maven 标准)
```text
quadra-social (父工程)
├── pom.xml
│
├── quadra-social-domain
│   ├── pom.xml
│   └── src/main/java/com/quadra/social/domain
│       ├── model
│       ├── service
│       ├── event
│       └── exception
│
├── quadra-social-application
│   ├── pom.xml (依赖 domain)
│   └── src/main/java/com/quadra/social/application
│       ├── port.in
│       ├── port.out
│       └── service
│
├── quadra-social-adapter
│   ├── pom.xml
│   ├── quadra-social-adapter-in-web
│   │   ├── pom.xml (依赖 application, Spring Web)
│   │   └── src/main/java/com/quadra/social/adapter/in/web
│   ├── quadra-social-adapter-in-mq
│   │   ├── pom.xml (依赖 application, MQ Client)
│   │   └── src/main/java/com/quadra/social/adapter/in/mq
│   └── quadra-social-adapter-out
│       ├── pom.xml (依赖 application, MyBatis-Plus, Redis)
│       └── src/main/java/com/quadra/social/adapter/out
│           ├── persistence
│           ├── cache
│           └── event
│
└── quadra-social-infrastructure
    ├── pom.xml (依赖所有子模块)
    ├── src/main/java/com/quadra/social/infrastructure
    │   ├── config
    │   └── QuadraSocialApplication.java
    └── src/main/resources
        ├── application.properties
        └── mapper
```

### 模块依赖规则 (强约束)
- domain 无外部依赖。
- application 仅依赖 domain。
- adapter-in/out 仅依赖 application；禁止 adapter-in 直接依赖 adapter-out。
- infrastructure 依赖所有子模块，负责装配与启动。

## 4. 服务定位
- 服务名：`quadra_social`
- 领域职责：好友、关注、互相喜欢匹配、访客、位置轨迹
- 目标：打通关系链路与事件闭环，作为 content/recommend 上游输入

## 5. 目标表
- `friendship`
- `user_follow`
- `user_match_like`
- `user_visitor`
- `user_location_track`
- `outbox_event`

## 6. 分层实施

### 6.1 Domain
- 聚合根：`Friendship`、`UserFollow`、`UserMatchLike`、`UserVisitor`
- 约束：
  - 不能自己关注自己
  - 不能自己与自己匹配
  - 双向好友写入两条关系记录

### 6.2 Application
- UseCase：
  - `FollowUserUseCase` / `UnfollowUserUseCase`
  - `SwipeLikeUseCase`（LIKE/DISLIKE）
  - `ListFollowersQuery` / `ListFollowingQuery`
  - `RecordVisitorUseCase`
- Port.out：
  - `FollowRepositoryPort`
  - `MatchRepositoryPort`
  - `VisitorRepositoryPort`
  - `EventPublisherPort`

### 6.3 Adapter-out
- MyBatis-Plus Repository 实现
- Outbox 事件落库
- Redis（可选）：
  - `follow:{userId}` Set
  - `fans:{userId}` Set
  - `geo:user`（附近的人）

### 6.4 Adapter-in
- REST：
  - `POST /api/v1/social/follows`
  - `DELETE /api/v1/social/follows/{targetUserId}`
  - `POST /api/v1/social/swipes`
  - `GET /api/v1/social/followers`
  - `GET /api/v1/social/following`
- MQ Listener（后续）：
  - 消费推荐/内容域回传事件

## 7. 核心难点与教学爆点处理方案
- 双向好友一致性：应用层本地事务包裹 A→B 与 B→A 两条记录，任一失败则回滚。
- 高频关注查询优化：关注关系同步至 Redis Set，读优先 Redis，异步回写 DB。
- 匹配事件生成：双 LIKE 判定后写出 `MatchCreatedEvent` 至 Outbox，供推荐域订阅。
- 附近的人：位置轨迹写入 Redis GEO，按半径与分页返回，持久化按天归档。

## 8. 里程碑
- M1：骨架 + DDL 落库
- M2：Follow/Unfollow + 分页查询
- M3：SwipeLike/Dislike + 匹配事件
- M4：Visitor + Outbox 定时投递
- M5：鉴权联调 + 回归测试

## 9. 验证标准
- `mvn -pl quadra-social-infrastructure -am clean install -DskipTests` 成功
- Swagger 可完整演示 follow/swipe/list 全流程
- Outbox 表可观察事件状态从 0 到 1 的变化
