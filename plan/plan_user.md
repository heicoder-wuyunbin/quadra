# Quadra User 服务 (quadra_user) 开发计划

## 1. 技术栈选型
- **JDK**: Java 21
- **框架**: Spring Boot 3.5.13
- **ORM**: MyBatis-Plus 3.5.15
- **数据库**: MySQL 8 (对应 `quadra_user` 库)
- **架构**: DDD 六边形架构 (Hexagonal Architecture)
- **配置**: `.properties` 格式

## 2. 领域建模映射

基于 `quadra_user.sql`，提取出以下三个核心聚合根及实体：

### 2.1 User 聚合根 (核心身份域)
- **聚合根**: `User` (用户账号)
- **实体**: `UserProfile` (用户资料，包含 JSON tags 处理)
- **实体**: `UserSetting` (用户偏好设置)
- *说明*: Profile 和 Setting 依附于 User，拥有强一致性的生命周期（1:1 共享主键）。创建用户时同步创建，注销用户时同步软删除。

### 2.2 Blacklist 聚合根 (关系防扰域)
- **聚合根**: `UserBlacklist`
- *说明*: 高频读写的用户关系，独立于 User 聚合。

### 2.3 Question 聚合根 (防骚扰提问域)
- **聚合根**: `StrangerQuestion`
- *说明*: 用户可配置的多个陌生人破冰问题。

## 3. 工程目录规划 (六边形多模块 Maven 标准)

为了实现物理级别的架构隔离并提升开发体验，将 `quadra-user` 设计为 Maven 多模块工程（Multi-Module Project）。每个架构层都是一个独立的子模块（Sub-module），强制通过 `pom.xml` 的依赖声明来控制层级调用关系，彻底杜绝跨层调用。

```text
quadra-user (父工程, 管理依赖版本)
├── pom.xml
│
├── quadra-user-domain (领域层模块)
│   ├── pom.xml (无任何外部框架依赖)
│   └── src/main/java/com/quadra/user/domain
│       ├── model                # 聚合根, 实体, 值对象
│       ├── service              # 领域服务
│       ├── event                # 领域事件
│       └── exception            # 领域异常
│
├── quadra-user-application (应用层模块)
│   ├── pom.xml (依赖 quadra-user-domain)
│   └── src/main/java/com/quadra/user/application
│       ├── port.in              # 用例接口 (Command/Query)
│       ├── port.out             # 防腐层接口 (UserRepositoryPort等)
│       └── service              # 应用服务实现
│
├── quadra-user-adapter (适配器层父模块)
│   ├── pom.xml
│   │
│   ├── quadra-user-adapter-in-web (入站 Web 模块)
│   │   ├── pom.xml (依赖 quadra-user-application, Spring Web)
│   │   └── src/main/java/com/quadra/user/adapter/in/web  # Controllers
│   │
│   ├── quadra-user-adapter-in-mq (入站 MQ 模块)
│   │   ├── pom.xml (依赖 quadra-user-application, MQ Client)
│   │   └── src/main/java/com/quadra/user/adapter/in/mq   # 消费者监听器
│   │
│   └── quadra-user-adapter-out (出站适配器模块)
│       ├── pom.xml (依赖 quadra-user-application, MyBatis-Plus, Redis)
│       └── src/main/java/com/quadra/user/adapter/out
│           ├── persistence      # MyBatis-Plus 实现、Mapper、DO
│           ├── cache            # Redis 实现
│           └── event            # Outbox 投递实现
│
└── quadra-user-infrastructure (基础设施/启动模块)
    ├── pom.xml (依赖所有子模块)
    ├── src/main/java/com/quadra/user/infrastructure
    │   ├── config               # 各种框架配置类
    │   └── QuadraUserApplication.java # Spring Boot 启动类
    └── src/main/resources
        ├── application.properties
        └── mapper               # MyBatis XML
```

### 模块依赖规则 (强约束)
- **`quadra-user-domain`**: 处于依赖最底层，没有任何依赖。
- **`quadra-user-application`**: 仅依赖 `domain` 模块。
- **`adapter-in-*` 与 `adapter-out`**: 仅依赖 `application` 模块。禁止互相依赖，禁止直接依赖 `domain`（需通过 application 透传）。
- **`quadra-user-infrastructure`**: 作为最终组装器，依赖所有模块，负责启动应用。

## 4. 业务规划与开发优先级 (开发节奏)

### Step 1: RegisterUserUseCase 全链路跑通 (基石)
1. **Controller**: 接收注册请求。
2. **ApplicationService**: 拦截校验、密码 BCrypt 加密、编排注册流程。
3. **Domain**: `User.register` 工厂方法，确保不出现无效状态。
4. **Repository**: 将 User 聚合根拆解为三张表的 DO 并在同事务中插入。
5. **Outbox**: 事务内同步写入发件箱表。

### Step 2: UserLoginUseCase
- 包含密码校验、生成 JWT Token (JWT 生成不进入 Domain，在 Application 或 Adapter 处理)。

### Step 3: Query 模型 (用户资料查询)
- 绕过 Domain 聚合根，使用 MyBatis-Plus 直接在 `UserQueryPort` 查询数据库，并返回用于展示的 DTO/VO。

## 5. 核心难点与教学爆点处理方案

1. **MySQL 8 JSON 映射 (替代 MongoDB 的关键)**: 
   - 难点：MyBatis-Plus 如何优雅处理 `tags` 字段的双向转换？
   - 方案：自定义 `JacksonTypeHandler` 挂载到 DO 的 `tags` 字段上。教学时可对比 `List<String>` 与 `Map<String, Object>` 两种模式，强调 JSON 在 DDD 中作为值对象的灵活性。
2. **聚合根的持久化 (Repository ≠ DAO)**:
   - 难点：保存 `User` 聚合根时，如何同时保存 `user`, `user_profile`, `user_setting` 三张表？
   - 方案：在 `Adapter.out` 的 `UserRepositoryImpl` 中，将 Domain 聚合根拆解为三个 DO（作为持久化快照），并在同一个本地事务中分别调用对应的 Mapper 插入数据库。向学生讲透“一个聚合要拆三张表”的底层逻辑。
3. **最终一致性 (Outbox 投递闭环)**:
   - 方案：
     - **写闭环**：在本地事务中（如 Spring 的 `TransactionPhase.BEFORE_COMMIT` 或直接同步写入），将事件写入 `outbox_event` 表，确保业务和事件强绑定。
     - **投递闭环**：设计定时任务扫描器，基于 `status=0` 或 `next_retry_time` 进行指数退避重试投递至 MQ。
     - **消费闭环**：MQ 消费者基于 `event_id` 做幂等处理。
4. **登录与防重复注册 (安全基线)**:
   - 方案：注册时结合 DB `mobile + deleted` 唯一索引兜底，并使用 BCrypt 加密密码；登录成功后生成 JWT Token 颁发给客户端。

## 6. 实施确认
请确认此计划（尤其是目录结构和技术栈版本）。如果计划通过，我们将立刻开始初始化 Spring Boot 3 工程并编写核心代码！