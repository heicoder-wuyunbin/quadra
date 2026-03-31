# Quadra System 服务实施计划

## 1. 技术栈选型
- JDK: Java 21
- 框架: Spring Boot 3.5.13
- ORM: MyBatis-Plus 3.5.15
- 数据库: MySQL 8 (库名 `quadra_system`)
- 架构: DDD 六边形架构 (Hexagonal)
- 配置: `.properties`

## 2. 领域建模映射
- SysAdmin 聚合根：后台管理员账号，含状态机（启用/禁用）。
- SysRole 聚合根：角色定义，角色编码唯一。
- SysMenu 聚合根：菜单/权限点定义。
- SysDataAnalysis 聚合根：日维度运营分析指标。
- 关系实体：`SysAdminRole`、`SysRoleMenu`。
- OutboxEvent：管理员创建、角色变更事件持久化（可选）。

## 3. 工程目录规划 (六边形多模块 Maven 标准)
```text
quadra-system (父工程)
├── pom.xml
│
├── quadra-system-domain
│   ├── pom.xml
│   └── src/main/java/com/quadra/system/domain
│       ├── model
│       ├── service
│       ├── event
│       └── exception
│
├── quadra-system-application
│   ├── pom.xml (依赖 domain)
│   └── src/main/java/com/quadra/system/application
│       ├── port.in
│       ├── port.out
│       └── service
│
├── quadra-system-adapter
│   ├── pom.xml
│   ├── quadra-system-adapter-in-web
│   │   ├── pom.xml (依赖 application, Spring Web)
│   │   └── src/main/java/com/quadra/system/adapter/in/web
│   └── quadra-system-adapter-out
│       ├── pom.xml (依赖 application, MyBatis-Plus, Scheduler)
│       └── src/main/java/com/quadra/system/adapter/out
│           ├── persistence
│           ├── audit
│           └── event
│
└── quadra-system-infrastructure
    ├── pom.xml (依赖所有子模块)
    ├── src/main/java/com/quadra/system/infrastructure
    │   ├── config
    │   └── QuadraSystemApplication.java
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
  - `SysAdmin`
  - `SysRole`
  - `SysMenu`
  - `SysDataAnalysis`
- 约束：
  - 用户名、角色编码唯一
  - 角色授权幂等
  - 管理员状态机（禁用/启用）

### 4.2 Application
- UseCase：
  - `AdminLoginUseCase`
  - `AssignRoleToAdminUseCase`
  - `GrantMenuToRoleUseCase`
  - `GenerateDailyAnalysisUseCase`
- Port.out：
  - `AdminRepositoryPort`
  - `RoleRepositoryPort`
  - `MenuRepositoryPort`
  - `AnalysisRepositoryPort`
  - `EventPublisherPort`

### 4.3 Adapter-out
- MyBatis-Plus 持久化
- 日志审计写入
- Outbox 事件推送（如管理员创建、角色变更）

### 4.4 Adapter-in
- REST：
  - `POST /api/v1/system/admin/login`
  - `POST /api/v1/system/admin/roles`
  - `POST /api/v1/system/roles/menus`
  - `GET /api/v1/system/analysis/daily`
- Scheduler：
  - 日维度统计汇总任务

## 5. 核心难点与教学爆点处理方案
- RBAC 权限模型：以角色聚合管理权限点，读取路径做缓存，避免 N+1。
- 管理端认证隔离：与用户端隔离 JWT Scope，权限拦截器单独实现。
- 审计日志：AOP 采集关键管理操作，失败回滚与日志写入一致性。
- 报表计算幂等：按业务日与任务批次作为幂等键，失败可重跑。

## 6. 里程碑
- M1：RBAC 主体（admin/role/menu）闭环
- M2：授权关系与权限读取
- M3：操作日志与审计
- M4：日报聚合任务

## 7. 验证标准
- RBAC API 可完成登录、授权、鉴权基本流程
- 审计日志可完整记录关键管理动作
- 每日分析数据可稳定产出
