import { systemApi } from './request';

export interface AdminLoginRequest {
  username: string;
  password: string;
}

export interface AdminLoginResultDTO {
  accessToken: string;
  refreshToken: string;
  expiresAt: string;
  adminInfo: {
    id: number;
    username: string;
    realName: string;
    avatar?: string;
  };
}

/**
 * 管理员登录
 */
export const adminLogin = async (data: AdminLoginRequest) => {
  return systemApi.post<AdminLoginResultDTO>('/api/system/admin/login', data);
};

/**
 * 刷新令牌
 */
export const refreshAdminToken = async (refreshToken: string) => {
  return systemApi.post<AdminLoginResultDTO>('/api/system/admin/refresh', {
    refreshToken,
  });
};

/**
 * 管理员登出
 */
export const adminLogout = async () => {
  return systemApi.post('/api/system/admin/logout');
};

/**
 * 获取管理员列表
 */
export const listAdmins = async (status?: number, page = 1, size = 10) => {
  return systemApi.get('/api/system/admins', {
    params: { status, page, size },
  });
};

/**
 * 创建管理员
 */
export const createAdmin = async (data: {
  username: string;
  password: string;
  realName: string;
}) => {
  return systemApi.post<number>('/api/system/admins', data);
};

/**
 * 分配角色
 */
export const assignRole = async (adminId: number, roleIds: number[]) => {
  return systemApi.post('/api/system/admin/roles', {
    adminId,
    roleIds,
  });
};
