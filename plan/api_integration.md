# 管理后台 API 联调说明

## 后端微服务端口

- **Gateway**: http://localhost:18080
- **System Service**: http://localhost:18060 (通过网关 `/v1/system/**`)
- **User Service**: http://localhost:18070 (通过网关 `/v1/users/**`)
- **Content Service**: http://localhost:18050 (通过网关 `/v1/content/**`)
- **Social Service**: http://localhost:18040 (通过网关 `/v1/social/**`)
- **Interaction Service**: http://localhost:18030 (通过网关 `/v1/interactions/**`)

## 网关路由配置

```yaml
- id: user-service
  uri: http://localhost:18070
  predicates:
    - Path=/v1/users/**
    
- id: system-service
  uri: http://localhost:18060
  predicates:
    - Path=/v1/system/**
    
- id: content-service
  uri: http://localhost:18050
  predicates:
    - Path=/v1/content/**
    
- id: social-service
  uri: http://localhost:18040
  predicates:
    - Path=/v1/social/**
    
- id: interaction-service
  uri: http://localhost:18030
  predicates:
    - Path=/v1/interactions/**
```

## 已实现的 API 接口

### 1. 用户管理模块

#### 1.1 用户列表
- **路径**: `GET /v1/users/admin/users`
- **参数**: 
  - page: 页码 (默认 1)
  - size: 每页大小 (默认 10)
  - mobile: 手机号 (可选)
  - status: 状态 (可选)
- **返回**: `PageResult<UserAdminDTO>`

#### 1.2 用户详情
- **路径**: `GET /v1/users/admin/users/{id}`
- **返回**: `UserDetailDTO`

#### 1.3 更新用户状态
- **路径**: `PUT /v1/users/admin/users/{id}/status`
- **请求体**: `{ "status": 1 }`

#### 1.4 重置用户密码
- **路径**: `POST /v1/users/admin/users/{id}/reset-password`
- **返回**: `{ "newPassword": "xxx" }`

#### 1.5 黑名单管理
- **获取黑名单列表**: `GET /v1/users/blacklists`
  - 参数：pageNo, pageSize, mobile
- **添加到黑名单**: `POST /v1/users/blacklists`
  - 请求体：`{ "targetUserId": 123, "reason": "xxx" }`
- **从黑名单移除**: `DELETE /v1/users/blacklists`
  - 请求体：`{ "targetUserId": 123 }`

### 2. 系统管理模块

#### 2.1 管理员列表
- **路径**: `GET /v1/system/admins`
- **参数**: page, size, username

#### 2.2 管理员详情
- **路径**: `GET /v1/system/admins/{id}`

#### 2.3 创建管理员
- **路径**: `POST /v1/system/admins`

#### 2.4 更新管理员
- **路径**: `PUT /v1/system/admins/{id}`

#### 2.5 删除管理员
- **路径**: `DELETE /v1/system/admins/{id}`

#### 2.6 批量更新状态
- **路径**: `PUT /v1/system/admins/batch/status`
- **请求体**: `{ "adminIds": [1, 2, 3], "status": 1 }`

#### 2.7 批量删除
- **路径**: `DELETE /v1/system/admins/batch`
- **请求体**: `{ "adminIds": [1, 2, 3] }`

#### 2.8 更新密码
- **路径**: `PUT /v1/system/admins/{id}/password`
- **请求体**: `{ "newPassword": "xxx" }`

#### 2.9 更新状态
- **路径**: `PUT /v1/system/admins/{id}/status`
- **请求体**: `{ "status": 1 }`

### 3. 内容审核模块（需要补充管理端接口）

当前内容模块主要是 C 端接口，管理后台需要的接口：

#### 3.1 动态审核（待实现）
- `GET /v1/content/admin/movements` - 获取动态列表
- `PUT /v1/content/admin/movements/{id}/approve` - 审核通过
- `PUT /v1/content/admin/movements/{id}/reject` - 审核拒绝

#### 3.2 视频审核（待实现）
- `GET /v1/content/admin/videos` - 获取视频列表
- `PUT /v1/content/admin/videos/{id}/approve` - 审核通过
- `PUT /v1/content/admin/videos/{id}/reject` - 审核拒绝

#### 3.3 举报管理（待实现）
- `GET /v1/content/admin/reports` - 获取举报列表
- `PUT /v1/content/admin/reports/{id}/handle` - 处理举报
- `PUT /v1/content/admin/reports/{id}/ignore` - 忽略举报

### 4. 社交管理模块（需要补充管理端接口）

#### 4.1 匹配记录（待实现）
- `GET /v1/social/admin/matches` - 获取匹配记录列表

#### 4.2 好友关系（待实现）
- `GET /v1/social/admin/friendships` - 获取好友关系列表

### 5. 互动管理模块（需要补充管理端接口）

#### 5.1 评论管理（待实现）
- `GET /v1/interactions/admin/comments` - 获取评论列表
- `DELETE /v1/interactions/admin/comments/{id}` - 删除评论

### 6. 消息推送模块（需要补充管理端接口）

#### 6.1 站内信管理（待实现）
- `GET /v1/message/admin/notices` - 获取站内信列表
- `POST /v1/message/admin/notices` - 发送站内信
- `DELETE /v1/message/admin/notices/{id}` - 删除站内信

## 认证方式

所有管理后台接口需要在请求头中携带 JWT Token：

```
Authorization: Bearer {accessToken}
```

## 前端 API 配置更新

已更新 `/src/services/api.ts` 中的路径：

```typescript
// 用户管理
userApi.getUsers: '/v1/users/admin/users'
userApi.getUserDetail: '/v1/users/admin/users/{id}'
userApi.updateUserStatus: '/v1/users/admin/users/{id}/status'
userApi.resetUserPassword: '/v1/users/admin/users/{id}/reset-password'
userApi.getBlacklist: '/v1/users/blacklists'
userApi.addToBlacklist: '/v1/users/blacklists'
userApi.removeFromBlacklist: '/v1/users/blacklists'
```

## 下一步工作

1. ✅ 用户管理模块 - API 已配置完成，可以联调
2. ⏳ 内容审核模块 - 需要后端补充管理端接口
3. ⏳ 社交管理模块 - 需要后端补充管理端接口
4. ⏳ 互动管理模块 - 需要后端补充管理端接口
5. ⏳ 消息推送模块 - 需要后端补充管理端接口

## 测试方法

### 1. 测试用户列表接口

```bash
# 先登录获取 token
curl -X POST "http://localhost:18080/v1/system/admin/login" \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 使用 token 访问用户列表
curl "http://localhost:18080/v1/users/admin/users?page=1&size=10" \
  -H "Authorization: Bearer {accessToken}"
```

### 2. 前端测试

启动前端开发服务器后，访问 http://localhost:5173，登录后即可测试各个页面。

## 注意事项

1. 所有管理端接口都需要认证
2. 部分模块（内容、社交、互动、消息）的后端管理接口尚未实现，当前前端页面使用模拟数据
3. 网关路由配置在 `quadra-gateway-module` 中
4. 前端代理配置在 `vite.config.ts` 中
