# Quadra 管理后台

基于 Vite + React + TypeScript + Ant Design 的管理后台项目

## 🚀 技术栈

- **框架**: React 19
- **构建工具**: Vite
- **语言**: TypeScript
- **UI 组件库**: Ant Design 6
- **路由**: React Router 7
- **HTTP 客户端**: Axios
- **包管理器**: pnpm

## 📦 项目结构

```
quadra-vite/
├── src/
│   ├── api/                      # API 客户端
│   │   ├── generated/            # 自动生成的 API
│   │   │   ├── core/             # 核心请求类
│   │   │   ├── models/           # 数据模型
│   │   │   ├── services/         # API 服务
│   │   │   └── index.ts          # 导出
│   │   ├── index.ts              # API 导出
│   │   ├── request.ts            # Axios 配置
│   │   ├── content.ts            # 内容模块 API
│   │   ├── system.ts             # 系统模块 API
│   │   └── user.ts               # 用户模块 API
│   ├── assets/                   # 静态资源
│   ├── components/               # 公共组件
│   ├── layouts/                  # 布局组件
│   │   └── AdminLayout.tsx       # 管理后台布局
│   ├── pages/                    # 页面
│   │   ├── Login.tsx             # 登录页
│   │   ├── Dashboard.tsx         # 仪表盘
│   │   ├── content/              # 内容管理
│   │   ├── user/                 # 用户管理
│   │   └── system/               # 系统管理
│   ├── utils/                    # 工具函数
│   ├── hooks/                    # 自定义 Hooks
│   ├── types/                    # TypeScript 类型定义
│   ├── App.tsx                   # 根组件
│   └── main.tsx                  # 入口文件
├── scripts/                      # 脚本文件
│   ├── merge-openapi.ts          # 获取并合并 OpenAPI 文档
│   └── fetch-openapi.ts          # 获取 OpenAPI 文档 (旧)
├── openapi/                      # OpenAPI 文档输出目录
│   ├── combined-api.json         # 合并后的 API
│   └── *-api.json                # 各微服务 API
└── package.json
```

## 🛠️ 开发指南

### 1. 安装依赖

```bash
pnpm install
```

### 2. 安装 Ant Design 和路由

```bash
pnpm add antd @ant-design/icons react-router-dom
```

### 3. 启动开发服务器

```bash
pnpm dev
```

### 4. 获取微服务 API 文档

确保 `/Users/wuyunbin/workspace/quadra/openapi` 目录下有以下文件：
- content.json
- interaction.json
- recommonend.json
- social.json
- system.json
- user.json

然后运行：

```bash
pnpm fetch:api
```

这将从上述目录读取并合并所有 OpenAPI 文档到 `quadra-vite/openapi` 目录。

### 5. 生成 API 类型定义

```bash
pnpm gen:api
```

这将自动生成 TypeScript 类型和 API 客户端到 `src/api/generated` 目录。

### 6. 启动开发服务器

```bash
pnpm dev
```

访问 http://localhost:3000 查看管理后台。

## 📝 使用示例

### 路径别名

项目已配置 `@` 路径别名，指向 `src` 目录：

```typescript
import { Button } from '@/components/Button';
import { login } from '@/api/system';
```

### 使用生成的 API 客户端

```typescript
import { SystemService } from '@/api/generated';

// 管理员登录
const handleLogin = async () => {
  try {
    const result = await SystemService.adminLogin({
      username: 'admin',
      password: '123456',
    });
    
    // 保存 token
    localStorage.setItem('access_token', result.data.accessToken);
    localStorage.setItem('refresh_token', result.data.refreshToken);
  } catch (error) {
    console.error('登录失败', error);
  }
};
```

### 使用自定义 API 客户端

```typescript
import { contentApi } from '@/api';

// 发布动态
const publish = async () => {
  const result = await contentApi.publishMovement({
    textContent: 'Hello World',
    state: 1,
  });
  console.log(result);
};
```

### 请求拦截器

已配置自动添加 Token：

```typescript
// 无需手动添加 Authorization header
// 拦截器会自动从 localStorage 获取并添加
const response = await contentApi.publishMovement(data);
```

### Ant Design 组件使用

```typescript
import { Button, Table, Card } from 'antd';
import { UserOutlined } from '@ant-design/icons';

const MyComponent = () => {
  return (
    <Card title="用户管理">
      <Button type="primary" icon={<UserOutlined />}>
        添加用户
      </Button>
    </Card>
  );
};
```

## 🎯 微服务架构

Quadra 系统包含 6 个微服务：

| 服务 | 说明 |
|------|------|
| Content | 内容服务 - 动态、视频管理 |
| Interaction | 互动服务 - 点赞、评论、分享 |
| Recommend | 推荐服务 - 用户和内容推荐 |
| Social | 社交服务 - 关注、私信、问答 |
| System | 系统服务 - 管理员、角色、权限 |
| User | 用户服务 - 用户注册、登录、资料 |

API 文档位于 `/Users/wuyunbin/workspace/quadra/openapi` 目录。

## 📚 开发规范

### 1. 组件开发

```typescript
// components/Button/index.tsx
import React from 'react';
import { Button as AntdButton } from 'antd';

interface ButtonProps {
  children: React.ReactNode;
  onClick?: () => void;
}

export const Button: React.FC<ButtonProps> = ({ children, onClick }) => {
  return (
    <AntdButton onClick={onClick}>
      {children}
    </AntdButton>
  );
};
```

### 2. API 开发

```typescript
// api/content.ts
import { contentApi } from './request';

export interface PublishMovementRequest {
  textContent: string;
  medias?: Media[];
}

export const publishMovement = async (data: PublishMovementRequest) => {
  return contentApi.post('/api/v1/content/movements', data);
};
```

### 3. 页面开发

```typescript
// pages/Dashboard.tsx
import React, { useEffect, useState } from 'react';
import { systemApi } from '@/api';

export const Dashboard: React.FC = () => {
  const [admins, setAdmins] = useState([]);

  useEffect(() => {
    fetchAdmins();
  }, []);

  const fetchAdmins = async () => {
    const result = await systemApi.listAdmins();
    setAdmins(result.list);
  };

  return <div>Dashboard</div>;
};
```

## 🔧 构建和部署

### 构建

```bash
pnpm build
```

### 预览

```bash
pnpm preview
```

### 部署

构建产物在 `dist` 目录，可部署到任意静态服务器。

## 📖 相关文档

- [Vite 文档](https://vitejs.dev/)
- [React 文档](https://react.dev/)
- [TypeScript 文档](https://www.typescriptlang.org/)
- [Ant Design 文档](https://ant.design/)
- [Axios 文档](https://axios-http.com/)
