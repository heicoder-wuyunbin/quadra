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
  NoticeDTO,
  NoticeQueryParams,
  SendNoticeRequest,
  MessageTemplateDTO,
  MessageTemplateQueryParams,
  AnnouncementDTO,
  AnnouncementQueryParams,
  PushRecordDTO,
  PushRecordQueryParams,
  OperationLogDTO,
  LoginLogDTO,
  ErrorLogDTO,
  ApiStatDTO,
  SlowSqlDTO,
  LogQueryParams,
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

  batchUpdateAdminStatus: (adminIds: number[], status: number) => {
    return request<any, ApiResult<void>>({
      url: '/v1/system/admins/status/batch',
      method: 'put',
      data: { adminIds, status },
    });
  },

  batchDeleteAdmins: (adminIds: number[]) => {
    return request<any, ApiResult<void>>({
      url: '/v1/system/admins/batch',
      method: 'delete',
      data: { adminIds },
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

  getUserDetail: (id: string) => {
    return request<any, ApiResult<UserDetailDTO>>({
      url: `/v1/system/users/${id}`,
      method: 'get',
    });
  },

  updateUserStatus: (id: string, status: number) => {
    return request<any, ApiResult<void>>({
      url: `/v1/system/users/${id}/status`,
      method: 'put',
      data: { status },
    });
  },

  resetUserPassword: (id: string) => {
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

// ---------------------------
// 消息推送（管理端）
// ---------------------------
export const messageApi = {
  listNotices: (params?: NoticeQueryParams) => {
    return request<any, ApiResult<PageResult<NoticeDTO>>>({
      url: '/v1/message/admin/notices',
      method: 'get',
      params,
    });
  },

  sendNotice: (data: SendNoticeRequest) => {
    return request<any, ApiResult<number>>({
      url: '/v1/message/admin/notices',
      method: 'post',
      data,
    });
  },

  deleteNotice: (id: number) => {
    return request<any, ApiResult<void>>({
      url: `/v1/message/admin/notices/${id}`,
      method: 'delete',
    });
  },

  listTemplates: (params?: MessageTemplateQueryParams) => {
    return request<any, ApiResult<PageResult<MessageTemplateDTO>>>({
      url: '/v1/message/admin/templates',
      method: 'get',
      params,
    });
  },

  createTemplate: (data: Omit<MessageTemplateDTO, 'id' | 'createdAt' | 'updatedAt'>) => {
    return request<any, ApiResult<number>>({
      url: '/v1/message/admin/templates',
      method: 'post',
      data,
    });
  },

  updateTemplate: (id: number, data: Partial<Omit<MessageTemplateDTO, 'id' | 'createdAt' | 'updatedAt'>>) => {
    return request<any, ApiResult<void>>({
      url: `/v1/message/admin/templates/${id}`,
      method: 'put',
      data,
    });
  },

  listAnnouncements: (params?: AnnouncementQueryParams) => {
    return request<any, ApiResult<PageResult<AnnouncementDTO>>>({
      url: '/v1/message/admin/announcements',
      method: 'get',
      params,
    });
  },

  createAnnouncement: (data: Omit<AnnouncementDTO, 'id' | 'createdAt' | 'updatedAt' | 'publishedAt' | 'publisherName'>) => {
    return request<any, ApiResult<number>>({
      url: '/v1/message/admin/announcements',
      method: 'post',
      data,
    });
  },

  updateAnnouncement: (
    id: number,
    data: Partial<Omit<AnnouncementDTO, 'id' | 'createdAt' | 'updatedAt' | 'publishedAt' | 'publisherName'>>
  ) => {
    return request<any, ApiResult<void>>({
      url: `/v1/message/admin/announcements/${id}`,
      method: 'put',
      data,
    });
  },

  publishAnnouncement: (id: number) => {
    return request<any, ApiResult<void>>({
      url: `/v1/message/admin/announcements/${id}/publish`,
      method: 'put',
    });
  },

  offlineAnnouncement: (id: number) => {
    return request<any, ApiResult<void>>({
      url: `/v1/message/admin/announcements/${id}/offline`,
      method: 'put',
    });
  },

  toggleAnnouncementTop: (id: number, isTop: boolean) => {
    return request<any, ApiResult<void>>({
      url: `/v1/message/admin/announcements/${id}/top`,
      method: 'put',
      data: { isTop },
    });
  },

  listRecords: (params?: PushRecordQueryParams) => {
    return request<any, ApiResult<PageResult<PushRecordDTO>>>({
      url: '/v1/message/admin/records',
      method: 'get',
      params,
    });
  },
};

// ---------------------------
// 日志监控（管理端）
// ---------------------------
export const logApi = {
  getOperationLogs: (params?: LogQueryParams) => {
    return request<any, ApiResult<PageResult<OperationLogDTO>>>({
      url: '/v1/log/operation',
      method: 'get',
      params,
    });
  },

  getLoginLogs: (params?: LogQueryParams) => {
    return request<any, ApiResult<PageResult<LoginLogDTO>>>({
      url: '/v1/log/login',
      method: 'get',
      params,
    });
  },

  getErrorLogs: (params?: LogQueryParams & { level?: string; service?: string; handled?: boolean }) => {
    return request<any, ApiResult<PageResult<ErrorLogDTO>>>({
      url: '/v1/log/error',
      method: 'get',
      params,
    });
  },

  markErrorHandled: (id: string, handled = true) => {
    return request<any, ApiResult<void>>({
      url: `/v1/log/error/${id}/handled`,
      method: 'put',
      data: { handled },
    });
  },

  getApiStats: (params?: LogQueryParams) => {
    return request<any, ApiResult<PageResult<ApiStatDTO>>>({
      url: '/v1/log/api',
      method: 'get',
      params,
    });
  },

  getSlowSql: (params?: LogQueryParams) => {
    return request<any, ApiResult<PageResult<SlowSqlDTO>>>({
      url: '/v1/log/slow-sql',
      method: 'get',
      params,
    });
  },
};
