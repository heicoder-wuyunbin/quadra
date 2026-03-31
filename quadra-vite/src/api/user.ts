import { userApi } from './request';

export interface RegisterRequest {
  mobile: string;
  password: string;
}

export interface LoginRequest {
  mobile: string;
  password: string;
}

export interface LoginResultDTO {
  accessToken: string;
  refreshToken: string;
  expiresAt: string;
  userInfo: {
    id: number;
    mobile: string;
    nickname?: string;
    avatar?: string;
  };
}

export interface UserProfileDTO {
  id: number;
  mobile: string;
  nickname?: string;
  avatar?: string;
  gender?: number;
  birthday?: string;
  city?: string;
  income?: string;
  profession?: string;
  marriage?: number;
  coverPic?: string;
  tags?: Record<string, any>;
}

/**
 * 用户注册
 */
export const register = async (data: RegisterRequest) => {
  return userApi.post<number>('/api/v1/users/register', data);
};

/**
 * 用户登录
 */
export const login = async (data: LoginRequest) => {
  return userApi.post<LoginResultDTO>('/api/v1/users/login', data);
};

/**
 * 刷新令牌
 */
export const refreshToken = async (refreshToken: string) => {
  return userApi.post<LoginResultDTO>('/api/v1/users/refresh', {
    refreshToken,
  });
};

/**
 * 用户登出
 */
export const logout = async () => {
  return userApi.post('/api/v1/users/logout');
};

/**
 * 获取用户资料
 */
export const getUserProfile = async (userId: number) => {
  return userApi.get<UserProfileDTO>(`/api/v1/users/${userId}/profile`);
};

/**
 * 更新用户资料
 */
export const updateProfile = async (
  userId: number,
  data: Partial<UserProfileDTO>
) => {
  return userApi.put(`/api/v1/users/${userId}/profile`, data);
};
