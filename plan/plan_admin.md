# Quadra 管理后台开发计划

## 📋 项目概述

Quadra 是一个相亲交友类小程序的管理后台，用于管理用户、内容审核、社交关系、互动数据、消息推送等核心功能。

### 技术栈
- **前端框架**: React 18 + TypeScript
- **UI 组件库**: Ant Design 5.x
- **路由**: React Router 6.x
- **状态管理**: React Hooks (useState, useEffect)
- **HTTP 客户端**: Axios
- **构建工具**: Vite

### 项目架构
```
quadra-vite/
├── src/
│   ├── layouts/           # 布局组件
│   ├── pages/             # 页面组件
│   ├── services/          # API 服务层
│   ├── utils/             # 工具函数
│   ├── router/            # 路由配置
│   └── api/               # API 定义（已废弃）
└── ...
```

---

## 🎯 核心功能模块

### 1. 系统管理 (quadra_system) ✅ 已完成

**功能描述**: 管理后台管理员账号、角色权限、菜单配置和数据分析

**页面清单**:
- ✅ `/system/admins` - 管理员管理
  - 管理员列表（分页、搜索）
  - 创建管理员
  - 编辑管理员（修改真实姓名）
  - 修改密码
  - 启用/禁用管理员（超级管理员不可禁用）
  - 批量启用/禁用
  - 批量删除（软删除）
  
- ✅ `/system/roles` - 角色管理（待开发）
- ✅ `/system/menus` - 菜单管理（待开发）
- ✅ `/system/analysis` - 数据分析（待开发）

**后端接口**:
- `GET /v1/system/admins` - 分页查询管理员列表
- `POST /v1/system/admins` - 创建管理员
- `PUT /v1/system/admins/{id}` - 更新管理员信息
- `PUT /v1/system/admins/{id}/password` - 修改密码
- `PUT /v1/system/admins/{id}/status` - 更新状态
- `PUT /v1/system/admins/status/batch` - 批量更新状态
- `DELETE /v1/system/admins/batch` - 批量删除

**开发状态**: ✅ 管理员管理已完成

---

### 2. 用户管理 (quadra_user) 🔥 优先级最高

**功能描述**: 管理 C 端用户账号、资料、审核状态和黑名单

**页面清单**:

#### 2.1 用户列表 (`/user/users`)
**功能**:
- 用户列表展示（分页、搜索）
- 按手机号/昵称搜索
- 查看用户详情
- 禁用/启用用户账号
- 重置用户密码
- 查看用户资料（含 JSON 字段解析）
- 查看用户设置
- 查看用户黑名单

**后端接口**:
- `GET /v1/user/users` - 分页查询用户列表
- `GET /v1/user/users/{id}` - 获取用户详情
- `PUT /v1/user/users/{id}/status` - 更新用户状态
- `PUT /v1/user/users/{id}/password` - 重置密码

#### 2.2 用户审核 (`/user/audit`)
**功能**:
- 待审核用户列表
- 审核通过/拒绝
- 查看审核资料（实名认证、头像等）
- 审核历史记录

**后端接口**:
- `GET /v1/user/users/audit/pending` - 待审核列表
- `POST /v1/user/users/{id}/audit` - 审核操作

#### 2.3 黑名单管理 (`/user/blacklist`)
**功能**:
- 全局黑名单列表
- 添加用户到黑名单
- 从黑名单移除
- 黑名单原因记录

**后端接口**:
- `GET /v1/user/blacklist` - 黑名单列表
- `POST /v1/user/blacklist` - 添加到黑名单
- `DELETE /v1/user/blacklist/{id}` - 移除黑名单

**开发状态**: ⏳ 待开发（优先级：P0）

---

### 3. 内容审核 (quadra_content) 🔥 优先级最高

**功能描述**: 审核用户发布的图文动态和短视频，处理举报内容

**页面清单**:

#### 3.1 动态审核 (`/content/movements`)
**功能**:
- 图文动态列表
- 查看动态详情（图片、文字）
- 审核通过/下架
- 批量操作
- 按状态筛选（待审核/已通过/已下架）

**后端接口**:
- `GET /v1/content/movements` - 动态列表
- `GET /v1/content/movements/{id}` - 动态详情
- `PUT /v1/content/movements/{id}/status` - 更新审核状态
- `DELETE /v1/content/movements/batch` - 批量下架

#### 3.2 视频审核 (`/content/videos`)
**功能**:
- 短视频列表
- 视频播放预览
- 查看视频元数据（封面、时长、转码状态）
- 审核通过/下架
- 批量操作

**后端接口**:
- `GET /v1/content/videos` - 视频列表
- `GET /v1/content/videos/{id}` - 视频详情
- `PUT /v1/content/videos/{id}/status` - 更新审核状态

#### 3.3 举报管理 (`/content/reports`)
**功能**:
- 举报列表
- 查看举报详情（举报类型、原因、举报对象）
- 处理举报（通过/驳回）
- 自动下架被多次举报的内容
- 举报统计

**后端接口**:
- `GET /v1/content/reports` - 举报列表
- `GET /v1/content/reports/{id}` - 举报详情
- `POST /v1/content/reports/{id}/handle` - 处理举报

**开发状态**: ⏳ 待开发（优先级：P0）

---

### 4. 社交管理 (quadra_social) 🔥 优先级中等

**功能描述**: 管理用户社交关系、匹配记录和访客

**页面清单**:

#### 4.1 匹配记录 (`/social/matches`)
**功能**:
- 用户匹配列表
- 查看匹配详情（双方用户、匹配时间）
- 匹配统计（每日匹配数、成功率）
- 异常匹配检测

**后端接口**:
- `GET /v1/social/matches` - 匹配列表
- `GET /v1/social/matches/stats` - 匹配统计

#### 4.2 好友关系 (`/social/friendships`)
**功能**:
- 好友关系列表
- 查看好友详情
- 解除好友关系
- 查看双向好友

**后端接口**:
- `GET /v1/social/friendships` - 好友列表
- `DELETE /v1/social/friendships/{id}` - 解除关系

**开发状态**: ⏳ 待开发（优先级：P1）

---

### 5. 互动管理 (quadra_interaction) 🔥 优先级中等

**功能描述**: 管理用户评论和互动数据

**页面清单**:

#### 5.1 评论管理 (`/interaction/comments`)
**功能**:
- 评论列表（分页、搜索）
- 查看评论详情
- 删除违规评论
- 批量删除
- 按目标类型筛选（动态/视频）

**后端接口**:
- `GET /v1/interaction/comments` - 评论列表
- `DELETE /v1/interaction/comments/{id}` - 删除评论
- `DELETE /v1/interaction/comments/batch` - 批量删除

**开发状态**: ⏳ 待开发（优先级：P1）

---

### 6. 消息推送 (新增模块) 🔥 优先级高

**功能描述**: 管理站内信、系统公告和消息推送

**页面清单**:

#### 6.1 站内信管理 (`/message/notices`)
**功能**:
- 站内信列表
- 创建站内信（支持选择用户）
- 发送站内信
- 查看已读/未读状态
- 定时发送

**后端接口**:
- `GET /v1/message/notices` - 站内信列表
- `POST /v1/message/notices` - 创建站内信
- `PUT /v1/message/notices/{id}/send` - 发送

#### 6.2 发送站内信 (`/message/send`)
**功能**:
- 选择接收用户（单个/批量/全员）
- 选择消息模板
- 编辑消息内容
- 立即发送/定时发送

#### 6.3 消息模板 (`/message/templates`)
**功能**:
- 模板列表
- 创建模板
- 编辑模板
- 模板变量说明

**后端接口**:
- `GET /v1/message/templates` - 模板列表
- `POST /v1/message/templates` - 创建模板
- `PUT /v1/message/templates/{id}` - 更新模板

#### 6.4 系统公告 (`/message/announcements`)
**功能**:
- 公告列表
- 创建公告
- 发布/下架公告
- 公告置顶

#### 6.5 推送记录 (`/message/records`)
**功能**:
- 推送历史记录
- 查看推送状态（成功/失败）
- 推送统计（送达率、已读率）

**开发状态**: ⏳ 待开发（优先级：P1）

---

### 7. 日志监控 (新增模块) 🔥 优先级高

**功能描述**: 记录管理员操作日志、系统错误日志和接口日志

**页面清单**:

#### 7.1 操作日志 (`/log/operation`)
**功能**:
- 操作日志列表
- 按管理员、模块、时间筛选
- 查看操作详情（请求参数、响应结果）
- 导出日志（Excel/CSV）
- 高亮危险操作

**数据模型**:
```typescript
interface OperationLog {
  id: string;
  adminId: number;
  adminName: string;
  module: string;
  action: string;
  targetId?: number;
  targetName?: string;
  requestParams: any;
  responseStatus: number;
  ip: string;
  userAgent: string;
  executeTime: number;
  createdAt: Date;
}
```

#### 7.2 登录日志 (`/log/login`)
**功能**:
- 登录记录列表
- 按用户、时间筛选
- 登录地点（IP 解析）
- 异常登录检测

#### 7.3 错误日志 (`/log/error`)
**功能**:
- 错误日志列表
- 按服务、错误级别筛选
- 查看错误堆栈
- 错误统计图表
- 标记已处理

**数据模型**:
```typescript
interface ErrorLog {
  id: string;
  level: 'ERROR' | 'WARN' | 'INFO';
  service: string;
  message: string;
  stackTrace: string;
  userId?: number;
  requestId: string;
  url: string;
  params: any;
  createdAt: Date;
}
```

#### 7.4 接口日志 (`/log/api`)
**功能**:
- 接口调用统计
- 接口响应时间排行
- 接口错误率监控
- 请求参数查看

#### 7.5 慢查询日志 (`/log/slow-sql`)
**功能**:
- 慢 SQL 列表
- SQL 执行时间
- SQL 执行计划
- 优化建议

**开发状态**: ⏳ 待开发（优先级：P1）

---

### 8. 运维监控 (新增模块) 🔥 优先级低

**功能描述**: 监控微服务健康状态、系统性能和告警

**页面清单**:

#### 8.1 服务监控 (`/monitor/services`)
**功能**:
- 服务列表（user/content/social/interaction/recommend/system）
- 服务健康状态
- 服务响应时间
- 服务调用量统计

#### 8.2 性能监控 (`/monitor/performance`)
**功能**:
- CPU 使用率
- 内存使用率
- 磁盘使用率
- JVM 监控（堆内存、GC）

#### 8.3 告警管理 (`/monitor/alerts`)
**功能**:
- 告警列表
- 告警规则配置
- 告警通知（站内信/邮件/短信）
- 告警处理记录

**开发状态**: ⏳ 待开发（优先级：P2）

---

## 📊 开发优先级

### P0 - 核心功能（必须）
1. ✅ 系统管理 - 管理员管理（已完成）
2. 🔥 用户管理 - 用户列表、用户详情、黑名单
3. 🔥 内容审核 - 动态审核、视频审核、举报管理

### P1 - 重要功能（应该有）
4. 社交管理 - 匹配记录、好友关系
5. 互动管理 - 评论管理
6. 消息推送 - 站内信、模板、公告
7. 日志监控 - 操作日志、错误日志

### P2 - 增强功能（可以有）
8. 用户审核 - 实名认证审核
9. 运维监控 - 服务监控、性能监控、告警管理

### P3 - 可选功能（锦上添花）
10. 系统管理 - 角色管理、菜单管理（权限系统）
11. 数据分析 - 运营数据可视化

---

## 🛠️ 技术实施

### 1. 前端架构设计

#### 目录结构
```
src/pages/
├── user/
│   ├── Users.tsx              # 用户列表
│   ├── UserProfile.tsx        # 用户详情
│   ├── Audit.tsx              # 用户审核
│   └── Blacklist.tsx          # 黑名单管理
├── content/
│   ├── Movements.tsx          # 动态审核
│   ├── Videos.tsx             # 视频审核
│   └── Reports.tsx            # 举报管理
├── social/
│   ├── Matches.tsx            # 匹配记录
│   └── Friendships.tsx        # 好友关系
├── interaction/
│   └── Comments.tsx           # 评论管理
├── message/
│   ├── Notices.tsx            # 站内信管理
│   ├── SendNotice.tsx         # 发送站内信
│   ├── Templates.tsx          # 消息模板
│   ├── Announcements.tsx      # 系统公告
│   └── Records.tsx            # 推送记录
├── log/
│   ├── OperationLogs.tsx      # 操作日志
│   ├── LoginLogs.tsx          # 登录日志
│   ├── ErrorLogs.tsx          # 错误日志
│   ├── ApiLogs.tsx            # 接口日志
│   └── SlowSQL.tsx            # 慢查询日志
├── monitor/
│   ├── Services.tsx           # 服务监控
│   ├── Performance.tsx        # 性能监控
│   └── Alerts.tsx             # 告警管理
└── system/
    ├── Admins.tsx             # 管理员管理 ✅
    ├── Roles.tsx              # 角色管理
    ├── Menus.tsx              # 菜单管理
    └── Analysis.tsx           # 数据分析
```

#### 通用组件封装
```typescript
// 1. 表格组件（带分页、搜索、批量操作）
interface ProTableProps<T> {
  columns: ColumnsType<T>;
  dataSource: T[];
  loading: boolean;
  pagination: PaginationConfig;
  onSearch: (values: any) => void;
  onBatchAction: (selectedKeys: Key[]) => void;
}

// 2. 搜索表单组件
interface SearchFormProps {
  fields: SearchField[];
  onSearch: (values: any) => void;
  onReset: () => void;
}

// 3. 详情弹窗组件
interface DetailModalProps {
  visible: boolean;
  data: any;
  onClose: () => void;
}
```

### 2. API 服务层设计

#### services/api.ts
```typescript
// 用户管理 API
export const userApi = {
  getUsers: (params: UserQueryParams) => request({ url: '/v1/user/users', method: 'get', params }),
  getUserById: (id: number) => request({ url: `/v1/user/users/${id}`, method: 'get' }),
  updateUserStatus: (id: number, status: number) => request({ url: `/v1/user/users/${id}/status`, method: 'put', data: { status } }),
  resetUserPassword: (id: number, password: string) => request({ url: `/v1/user/users/${id}/password`, method: 'put', data: { password } }),
};

// 内容审核 API
export const contentApi = {
  getMovements: (params: MovementQueryParams) => request({ url: '/v1/content/movements', method: 'get', params }),
  auditMovement: (id: number, status: number, reason?: string) => request({ url: `/v1/content/movements/${id}/status`, method: 'put', data: { status, reason } }),
  // ...
};

// 日志监控 API
export const logApi = {
  getOperationLogs: (params: LogQueryParams) => request({ url: '/v1/log/operation', method: 'get', params }),
  getErrorLogs: (params: LogQueryParams) => request({ url: '/v1/log/error', method: 'get', params }),
  // ...
};
```

### 3. 状态管理策略

- **简单状态**: 使用 `useState` + `useEffect`
- **复杂状态**: 使用 `useReducer` 或 Context
- **表单状态**: 使用 Ant Design `Form.useForm()`
- **缓存数据**: 使用 `localStorage` 或 `sessionStorage`

---

## 📝 开发规范

### 1. 代码规范
- ✅ 使用 TypeScript 严格模式
- ✅ 组件使用函数式 + Hooks
- ✅ 接口定义放在 `services/types.ts`
- ✅ 常量统一大写，放在文件顶部
- ✅ 错误处理使用 try-catch + message 提示

### 2. 命名规范
- 文件名：PascalCase（如 `Users.tsx`）
- 组件名：PascalCase（如 `const Users: React.FC = () => {}`）
- 函数名：camelCase（如 `handleSearch`）
- 常量名：UPPER_SNAKE_CASE（如 `const PAGE_SIZE = 10`）

### 3. 注释规范
- 文件顶部注释说明功能
- 复杂函数添加 JSDoc 注释
- TODO 注释标记待优化内容

---

## 🎯 里程碑

### M1 - 基础框架搭建 ✅
- [x] 项目结构整理
- [x] 路由配置
- [x] 菜单配置
- [x] 管理员管理功能

### M2 - 用户管理模块（预计 3 天）
- [ ] 用户列表页面
- [ ] 用户详情页面
- [ ] 黑名单管理
- [ ] 后端接口联调

### M3 - 内容审核模块（预计 3 天）
- [ ] 动态审核页面
- [ ] 视频审核页面
- [ ] 举报管理页面
- [ ] 后端接口联调

### M4 - 消息推送模块（预计 2 天）
- [ ] 站内信管理
- [ ] 消息模板
- [ ] 推送记录
- [ ] 后端接口联调

### M5 - 日志监控模块（预计 2 天）
- [ ] 操作日志
- [ ] 错误日志
- [ ] 后端接口联调

### M6 - 其他模块（预计 2 天）
- [ ] 社交管理
- [ ] 互动管理
- [ ] 运维监控

---

## 📊 总结

### 已完成
- ✅ 项目结构整理
- ✅ 路由和菜单配置
- ✅ 管理员管理（含批量操作、固定列、面包屑导航）

### 待开发（按优先级）
1. 🔥 用户管理（P0）
2. 🔥 内容审核（P0）
3. 🔥 消息推送（P1）
4. 🔥 日志监控（P1）
5. 📊 社交管理（P1）
6. 📊 互动管理（P1）
7. 📊 运维监控（P2）

### 技术亮点
- 🎨 Ant Design 5.x 最新组件
- 📱 响应式布局
- 🔐 权限控制（待实现）
- 📊 数据可视化（待实现）
- 🚀 高性能表格（固定列、虚拟滚动）

---

**最后更新**: 2026-04-01
**作者**: Quadra Team
