import axios from 'axios';

// 兼容 src/api/* 与 openapi 生成代码的请求方式
// 重要：管理后台所有请求必须走网关（同源 + /v1 前缀，dev 环境由 vite proxy 转发到 gateway）
const client = axios.create({
  baseURL: '/',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

client.interceptors.request.use((config) => {
  const accessToken = localStorage.getItem('access_token');
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});

export const systemApi = client;
export const contentApi = client;

