import axios from 'axios';
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios';
import { message } from 'antd';

// 扩展 Axios 配置类型
type AuthScope = 'admin' | 'user' | 'none';

interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  skipErrorHandler?: boolean;
  authScope?: AuthScope;
}

const isAdminPath = (url?: string) => {
  if (!url) return false;
  return url.includes('/v1/system');
};

const isUserPath = (url?: string) => {
  if (!url) return false;
  return (
    url.includes('/v1/users') ||
    url.includes('/v1/content') ||
    url.includes('/v1/social') ||
    url.includes('/v1/interaction') ||
    url.includes('/v1/interactions') ||
    url.includes('/v1/recommend') ||
    url.includes('/v1/recommends')
  );
};

const inferAuthScope = (url?: string): AuthScope => {
  if (isAdminPath(url)) return 'admin';
  if (isUserPath(url)) return 'user';
  return 'none';
};

const service: AxiosInstance = axios.create({
  baseURL: '',
  timeout: 15000,
});

service.interceptors.request.use(
  (config) => {
    const typedConfig = config as CustomAxiosRequestConfig;
    const scope = typedConfig.authScope ?? inferAuthScope(config.url);
    if (scope === 'admin') {
      const token = localStorage.getItem('access_token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    } else if (scope === 'user') {
      const token = localStorage.getItem('user_access_token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    }
    // 默认添加 Content-Type
    if (!config.headers['Content-Type'] && !config.headers['content-type']) {
      config.headers['Content-Type'] = 'application/json';
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data;
    
    // 处理成功响应（支持多种返回格式）
    const isSuccess = res.success === true || res.code === 'SUCCESS' || res.code === 20000 || res.code === 200;
    
    if (!isSuccess) {
      // 如果配置了 skipErrorHandler，则不显示错误提示
      const config = response.config as unknown as CustomAxiosRequestConfig;
      if (!config.skipErrorHandler) {
        message.error(res.message || 'Error');
      }
      return Promise.reject(new Error(res.message || 'Error'));
    }
    return res;
  },
  (error) => {
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // 只在非登录请求时跳转，且确保只在登录态有效时跳转
          {
            const config = error.config as unknown as CustomAxiosRequestConfig;
            const scope = config.authScope ?? inferAuthScope(config.url);
            if (scope === 'admin' && !error.config?.url?.includes('/login')) {
              const token = localStorage.getItem('access_token');
              if (token) {
                if (!config.skipErrorHandler) {
                  message.error('登录已过期，请重新登录');
                }
                localStorage.removeItem('access_token');
                localStorage.removeItem('refresh_token');
                window.location.href = '/login';
              }
            } else if (!config.skipErrorHandler) {
              message.error('未授权访问');
            }
          }
          break;
        case 403:
          {
            const config = error.config as unknown as CustomAxiosRequestConfig;
            if (!config.skipErrorHandler) {
              message.error('Forbidden');
            }
          }
          break;
        case 404:
          {
            const config = error.config as unknown as CustomAxiosRequestConfig;
            if (!config.skipErrorHandler) {
              message.error('Not Found');
            }
          }
          break;
        case 500:
          // 500 错误不显示 message，只记录日志
          console.error('Server error:', error.response.data);
          break;
        default:
          {
            const config = error.config as unknown as CustomAxiosRequestConfig;
            if (!config.skipErrorHandler) {
              message.error(error.response.data?.message || 'Error');
            }
          }
      }
    } else {
      // 网络错误，如果是 abort 则不显示错误
      if (error.code !== 'ERR_CANCELED' && !error.message.includes('abort')) {
        message.error('Network Error');
      }
    }
    return Promise.reject(error);
  }
);

export default service;
