import axios, { type AxiosRequestConfig, type AxiosResponse } from 'axios';
import type { ApiResult } from '@/services/types';

// 创建 axios 实例，所有请求都通过网关
// 在开发环境中，Vite 会代理 /v1 路径到 http://localhost:18080
const apiClient = axios.create({
  baseURL: '/',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器 - 添加认证 Token
apiClient.interceptors.request.use(
  (config) => {
    console.log('发起请求:', config.method?.toUpperCase(), config.url);
    // 从 localStorage 获取 accessToken
    const accessToken = localStorage.getItem('access_token');
    if (accessToken) {
      // 管理端鉴权：使用 Authorization: Bearer <token>
      // 若 token 已包含 Bearer 前缀，则直接透传，避免重复拼接。
      config.headers.Authorization = accessToken.startsWith('Bearer ') ? accessToken : `Bearer ${accessToken}`;
      console.log('已添加 Token');
    } else {
      console.log('未找到 Token，使用公开访问');
    }
    return config;
  },
  (error) => {
    console.error('请求拦截器错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器 - 处理错误
apiClient.interceptors.response.use(
  (response) => {
    // 返回响应数据，这样 response.data 就是 API 返回的内容
    return response;
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response;
      const requestUrl: string = error.config?.url || '';
      // 管理后台：所有请求都经由网关（统一 /api 前缀）
      const isAdminApi = requestUrl.startsWith('/api');
      
      if (status === 401) {
        // 仅对管理端接口触发强制登出，避免普通用户接口误伤
        if (isAdminApi) {
          console.error('认证失败，请重新登录');
          
          // 检查是否是刚刚登录后的请求
          const loginTime = localStorage.getItem('login_time');
          const currentTime = Date.now();
          const isRecentlyLoggedIn = loginTime && (currentTime - parseInt(loginTime)) < 5000; // 5秒内视为刚刚登录
          
          if (!isRecentlyLoggedIn) {
            // 不是刚刚登录的请求，才清除token并跳转
            localStorage.removeItem('access_token');
            localStorage.removeItem('refresh_token');
            localStorage.removeItem('login_time');

            // 只有在已登录的情况下才跳转，避免循环跳转
            const currentPath = window.location.pathname;
            if (currentPath !== '/login') {
              // 使用 setTimeout 避免在请求拦截器中直接跳转
              setTimeout(() => {
                window.location.href = '/login';
              }, 100);
            }
          }
        }
        return Promise.reject({ response: error.response, message: data?.message || '未授权' });
      } else if (status === 403) {
        console.error('没有权限访问该资源');
        return Promise.reject({ response: error.response, message: '没有权限访问该资源' });
      } else if (status === 404) {
        console.error('请求的资源不存在');
        return Promise.reject({ response: error.response, message: '请求的资源不存在' });
      } else if (status >= 500) {
        console.error('服务器错误');
        return Promise.reject({ response: error.response, message: '服务器错误' });
      }
      
      // 抛出包含响应数据的错误
      return Promise.reject({ response: error.response, message: data?.message || error.message });
    } else if (error.request) {
      console.error('网络错误，请检查网络连接');
      return Promise.reject({ message: '网络错误，请检查网络连接' });
    } else {
      console.error('请求错误:', error.message);
      return Promise.reject({ message: error.message });
    }
  }
);

// 通用请求方法
const request = async <D = unknown, R = unknown>(
  config: AxiosRequestConfig<D>
): Promise<AxiosResponse<R>> => {
  return apiClient.request<R, AxiosResponse<R>, D>(config);
};

/**
 * 最佳实践：管理后台统一使用后端标准返回结构 Result<T>，这里直接解包成 T。
 * - 成功：返回 data
 * - 失败：抛出 Error（包含 code/message/requestId）
 *
 * 说明：管理后台请求统一使用 /api 前缀，通过 Vite proxy/生产网关转发到后端真实服务路径（不含 /v1）。
 */
export const requestApi = async <T, D = unknown>(
  config: AxiosRequestConfig<D>
): Promise<T> => {
  const res = await request<D, ApiResult<T>>(config);
  const body = res.data;

  // 兼容极端情况：如果后端没包 Result<T>（不推荐），则直接返回
  if (!body || typeof body !== 'object' || !('success' in body) || !('data' in body)) {
    return body as unknown as T;
  }

  const result = body as ApiResult<T>;
  if (result.success) return result.data;

  const err: Error & { code?: number; requestId?: string } = new Error(result.message || '请求失败');
  err.code = result.code;
  err.requestId = result.requestId;
  throw err;
};

export default request;
