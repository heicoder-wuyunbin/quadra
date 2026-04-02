# 请求配置说明

## 配置变更

### 1. 统一通过网关访问
所有 API 请求现在都通过网关 `http://localhost:18080` 访问，由网关负责路由到对应的微服务。

### 2. 请求拦截器
在 `/src/utils/request.ts` 中配置了全局请求拦截器：

```typescript
apiClient.interceptors.request.use((config) => {
  const accessToken = localStorage.getItem('access_token');
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});
```

**特点**：
- 自动从 `localStorage` 获取 `access_token`
- 自动添加 `Bearer` 前缀到请求头
- 所有请求统一处理认证

### 3. 响应拦截器
```typescript
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // 清除 token 并跳转到登录页
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

**特点**：
- 401 自动跳转登录
- 统一的错误处理
- 友好的错误提示

## Token 管理

### 存储键名
- `access_token` - 访问令牌
- `refresh_token` - 刷新令牌

### 登录流程
```typescript
// Login.tsx
const res = await adminApi.login(values);
if (res.data && res.data.data) {
  const { accessToken, refreshToken } = res.data.data;
  localStorage.setItem('access_token', accessToken);
  localStorage.setItem('refresh_token', refreshToken);
}
```

### API 响应结构
后端返回的 API 格式：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "accessToken": "xxx",
    "refreshToken": "yyy"
  }
}
```

前端访问方式：
```typescript
const response = await userApi.getUsers(params);
// response.data 是 axios 响应
// response.data.data 是后端返回的 Result 对象
// response.data.data.data 是实际的业务数据
```

## 网关路由

| 路径前缀 | 目标服务 | 端口 |
|---------|---------|------|
| `/v1/system/**` | system-service | 18060 |
| `/v1/users/**` | user-service | 18070 |
| `/v1/content/**` | content-service | 18050 |
| `/v1/social/**` | social-service | 18040 |
| `/v1/interactions/**` | interaction-service | 18030 |

## 使用示例

### 1. 用户列表
```typescript
import { userApi } from '@/services/api';

const response = await userApi.getUsers({
  page: 1,
  size: 10,
  mobile: '13800138000',
  status: 1
});

// 获取数据
const users = response.data.data.data.records;
const total = response.data.data.data.total;
```

### 2. 更新用户状态
```typescript
await userApi.updateUserStatus(userId, status);
// 自动添加 Bearer Token
```

### 3. 黑名单管理
```typescript
await userApi.addToBlacklist(targetUserId, reason);
await userApi.removeFromBlacklist(targetUserId);
```

## 错误处理

### 401 未授权
- 自动清除 token
- 自动跳转到登录页
- 无需手动处理

### 403 禁止访问
```typescript
try {
  await someApi();
} catch (error: any) {
  if (error.message) {
    message.error(error.message);
  }
}
```

### 404 资源不存在
```typescript
try {
  await someApi();
} catch (error: any) {
  message.error('请求的资源不存在');
}
```

### 500 服务器错误
```typescript
try {
  await someApi();
} catch (error: any) {
  message.error('服务器错误');
}
```

## 调试技巧

### 1. 查看请求日志
在浏览器控制台可以看到：
- 请求响应数据
- Token 存储情况
- 错误信息

### 2. 查看 Network 面板
- 请求 URL
- 请求头（包含 Authorization）
- 响应数据

### 3. 查看 LocalStorage
- `access_token` - 当前登录凭证
- `refresh_token` - 刷新凭证

## 注意事项

1. **Token 存储**：必须使用 `access_token` 和 `refresh_token` 作为键名
2. **Bearer 前缀**：请求拦截器自动添加，无需手动处理
3. **响应解包**：需要正确理解 `response.data.data` 的嵌套结构
4. **错误处理**：401 会自动处理，其他错误需要手动捕获

## 相关文件

- `/src/utils/request.ts` - HTTP 请求封装
- `/src/utils/storage.ts` - Token 存储工具
- `/src/services/api.ts` - API 接口定义
- `/src/pages/Login.tsx` - 登录页面
