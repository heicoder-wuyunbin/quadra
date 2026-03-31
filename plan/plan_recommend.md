# Quadra Recommend 服务实施计划

## 1. 技术栈选型
- JDK: Java 21
- 框架: Spring Boot 3.5.13
- ORM: MyBatis-Plus 3.5.15
- 数据库: MySQL 8 (库名 `quadra_recommend`)
- 架构: DDD 六边形架构 (Hexagonal)
- 配置: `.properties`

## 2. 领域建模映射
- UserActionLog 聚合根：用户行为日志，记录 VIEW/LIKE/SKIP/DISLIKE 等动作。
- UserFeature 聚合根：基于行为与关系的用户特征。
- ContentFeature 聚合根：内容特征，含标签、画像、质量分。
- RecommendUser/RecommendContent 聚合根：推荐结果快照与分页读取。
- OutboxEvent：画像/结果构建完成事件持久化（可选）。

## 3. 工程目录规划 (六边形多模块 Maven 标准)
```text
quadra-recommend (父工程)
├── pom.xml
│
├── quadra-recommend-domain
│   ├── pom.xml
│   └── src/main/java/com/quadra/recommend/domain
│       ├── model
│       ├── service
│       ├── event
│       └── exception
│
├── quadra-recommend-application
│   ├── pom.xml (依赖 domain)
│   └── src/main/java/com/quadra/recommend/application
│       ├── port.in
│       ├── port.out
│       └── service
│
├── quadra-recommend-adapter
│   ├── pom.xml
│   ├── quadra-recommend-adapter-in-web
│   │   ├── pom.xml (依赖 application, Spring Web)
│   │   └── src/main/java/com/quadra/recommend/adapter/in/web
│   └── quadra-recommend-adapter-out
│       ├── pom.xml (依赖 application, MyBatis-Plus, Scheduler)
│       └── src/main/java/com/quadra/recommend/adapter/out
│           ├── persistence
│           ├── scheduler
│           └── event
│
└── quadra-recommend-infrastructure
    ├── pom.xml (依赖所有子模块)
    ├── src/main/java/com/quadra/recommend/infrastructure
    │   ├── config
    │   └── QuadraRecommendApplication.java
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
- 聚合根：
  - `UserActionLog`
  - `UserFeature`
  - `ContentFeature`
  - `RecommendUser`
  - `RecommendContent`
- 约束：
  - 行为类型合法（VIEW/LIKE/SKIP/DISLIKE）
  - 分值与策略规则分离

### 4.2 Application
- UseCase：
  - `RecordActionUseCase`
  - `BuildUserFeatureUseCase`
  - `BuildRecommendUserUseCase`
  - `GetRecommendUsersQuery`
  - `GetRecommendContentsQuery`
- Port.out：
  - `ActionRepositoryPort`
  - `FeatureRepositoryPort`
  - `RecommendRepositoryPort`

### 4.3 Adapter-out
- MyBatis-Plus 持久化
- 定时任务：
  - 统计行为权重
  - 更新 user/content 特征
  - 生成推荐结果缓存表
- 预留 AI 向量字段与计算接口

### 4.4 Adapter-in
- REST：
  - `POST /api/v1/recommends/actions`
  - `GET /api/v1/recommends/users`
  - `GET /api/v1/recommends/contents`
- Scheduler：
  - 离线构建特征与结果

## 5. 核心难点与教学爆点处理方案
- 数据分层：行为日志→特征→结果的分层建模，读写路径清晰解耦。
- 规则与数据解耦：评分规则外置，保留可替换性，便于教学演示。
- 批量任务幂等：离线调度按业务日维度，幂等键保障重复执行安全。
- 向量能力预留：设计 embedding 字段与扩展端口，后续可接超参服务。

## 6. 里程碑
- M1：行为日志写入
- M2：推荐结果读取接口
- M3：离线任务（规则版）落地
- M4：引入向量能力（可选增强）

## 7. 验证标准
- 推荐接口可稳定返回分页结果
- 离线任务可按日刷新推荐数据
- 行为记录可追溯并支撑画像更新
