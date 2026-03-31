import request from '@/utils/request';
import type {
  ApiResult,
  AdminLoginRequest,
  AdminLoginResultDTO,
  AdminDTO,
  CreateAdminRequest,
  CreateRoleRequest,
  CreateMenuRequest,
  MenuTreeDTO,
  AssignRoleRequest,
  GrantMenuRequest,
  DailyAnalysisDTO,
  PageResult,
  UserAdminDTO,
  UserDetailDTO,
} from './types';

export const adminApi = {
  login: (data: AdminLoginRequest) => {
    return request<any, ApiResult<AdminLoginResultDTO>>({
      url: '/v1/system/admin/login',
      method: 'post',
      data,
    });
  },

  refresh: (refreshToken: string) => {
    return request<any, ApiResult<AdminLoginResultDTO>>({
      url: '/v1/system/admin/refresh',
      method: 'post',
      data: { refreshToken },
    });
  },

  logout: () => {
    return request<any, ApiResult<void>>({
      url: '/v1/system/admin/logout',
      method: 'post',
    });
  },

  listAdmins: (params?: { status?: number; page?: number; size?: number }) => {
    return request<any, ApiResult<PageResult<AdminDTO>>>({
      url: '/v1/system/admins',
      method: 'get',
      params,
    });
  },

  createAdmin: (data: CreateAdminRequest) => {
    return request<any, ApiResult<number>>({
      url: '/v1/system/admins',
      method: 'post',
      data,
    });
  },

  updateAdmin: (id: number, data: { realName: string }) => {
    return request<any, ApiResult<void>>({
      url: `/v1/system/admins/${id}`,
      method: 'put',
      data,
    });
  },

  updateAdminPassword: (id: number, data: { password: string }) => {
    return request<any, ApiResult<void>>({
      url: `/v1/system/admins/${id}/password`,
      method: 'put',
      data,
    });
  },

  updateAdminStatus: (id: number, status: number) => {
    return request<any, ApiResult<void>>({
      url: `/v1/system/admins/${id}/status`,
      method: 'put',
      data: { status },
    });
  },

  assignRole: (data: AssignRoleRequest) => {
    return request<any, ApiResult<void>>({
      url: '/v1/system/admin/roles',
      method: 'post',
      data,
    });
  },

  getMenuTree: () => {
    return request<any, ApiResult<MenuTreeDTO[]>>({
      url: '/v1/system/menus/tree',
      method: 'get',
    });
  },

  createMenu: (data: CreateMenuRequest) => {
    return request<any, ApiResult<number>>({
      url: '/v1/system/menus',
      method: 'post',
      data,
    });
  },

  createRole: (data: CreateRoleRequest) => {
    return request<any, ApiResult<number>>({
      url: '/v1/system/roles',
      method: 'post',
      data,
    });
  },

  grantMenu: (data: GrantMenuRequest) => {
    return request<any, ApiResult<void>>({
      url: '/v1/system/roles/menus',
      method: 'post',
      data,
    });
  },

  getDailyAnalysis: (date: string) => {
    return request<any, ApiResult<DailyAnalysisDTO>>({
      url: '/v1/system/analysis/daily',
      method: 'get',
      params: { date },
    });
  },

  listUsers: (params?: { mobile?: string; status?: number; page?: number; size?: number }) => {
    return request<any, ApiResult<PageResult<UserAdminDTO>>>({
      url: '/v1/system/users',
      method: 'get',
      params,
    });
  },

  getUserDetail: (id: number) => {
    return request<any, ApiResult<UserDetailDTO>>({
      url: `/v1/system/users/${id}`,
      method: 'get',
    });
  },

  updateUserStatus: (id: number, status: number) => {
    return request<any, ApiResult<void>>({
      url: `/v1/system/users/${id}/status`,
      method: 'put',
      data: { status },
    });
  },

  resetUserPassword: (id: number) => {
    return request<any, ApiResult<{ newPassword: string }>>({
      url: `/v1/system/users/${id}/reset-password`,
      method: 'post',
    });
  },
};

export const contentApi = {
  timeline: (params?: { pageNo?: number; pageSize?: number }) => {
    return request<any, ApiResult<any>>({
      url: '/v1/content/timeline',
      method: 'get',
      params,
    });
  },
};

export const socialApi = {
  followers: (params?: { pageNo?: number; pageSize?: number }) => {
    return request<any, ApiResult<any>>({
      url: '/v1/social/followers',
      method: 'get',
      params,
    });
  },

  following: (params?: { pageNo?: number; pageSize?: number }) => {
    return request<any, ApiResult<any>>({
      url: '/v1/social/following',
      method: 'get',
      params,
    });
  },

  getRecommendUsers: (params?: { pageNum?: number; pageSize?: number }) => {
    return request({
      url: '/v1/recommends/users',
      method: 'get',
      params,
    });
  },
};
