import axios from 'axios';

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
      // 添加 Bearer 前缀
      config.headers.Authorization = `Bearer ${accessToken}`;
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
      const isAdminApi = requestUrl.startsWith('/v1/system');
      
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
const request = async (config: any) => {
  return apiClient.request(config);
};

export default request;
