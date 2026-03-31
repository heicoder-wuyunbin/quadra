import axios, { AxiosInstance, AxiosRequestConfig } from 'axios';

// API 基础 URL 配置
const API_BASE_URLS = {
  content: 'http://localhost:18081',
  interaction: 'http://localhost:18082',
  recommend: 'http://localhost:18083',
  social: 'http://localhost:18084',
  system: 'http://localhost:18085',
  user: 'http://localhost:18086',
};

// 创建 axios 实例
const createApiClient = (baseUrl: string): AxiosInstance => {
  const client = axios.create({
    baseURL: baseUrl,
    timeout: 10000,
    headers: {
      'Content-Type': 'application/json',
    },
  });

  // 请求拦截器
  client.interceptors.request.use(
    (config) => {
      // 从 localStorage 获取 token
      const token = localStorage.getItem('access_token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  // 响应拦截器
  client.interceptors.response.use(
    (response) => response.data,
    (error) => {
      if (error.response?.status === 401) {
        // Token 过期，跳转到登录页
        localStorage.removeItem('access_token');
        window.location.href = '/login';
      }
      return Promise.reject(error);
    }
  );

  return client;
};

// 导出各个微服务的 API 客户端
export const contentApi = createApiClient(API_BASE_URLS.content);
export const interactionApi = createApiClient(API_BASE_URLS.interaction);
export const recommendApi = createApiClient(API_BASE_URLS.recommend);
export const socialApi = createApiClient(API_BASE_URLS.social);
export const systemApi = createApiClient(API_BASE_URLS.system);
export const userApi = createApiClient(API_BASE_URLS.user);

// 通用请求方法
export const request = async <T>(config: AxiosRequestConfig): Promise<T> => {
  const response = await axios(config);
  return response.data;
};
