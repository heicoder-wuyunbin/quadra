import { requestApi } from '@/utils/request';
import type {
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
  RequestLogDTO,
  RequestLogQueryParams,
} from './types';

export const adminApi = {
  login: (data: AdminLoginRequest) => {
    return requestApi<AdminLoginResultDTO, AdminLoginRequest>({
      url: '/api/system/admin/login',
      method: 'post',
      data,
    });
  },

  refresh: (refreshToken: string) => {
    return requestApi<AdminLoginResultDTO, { refreshToken: string }>({
      url: '/api/system/admin/refresh',
      method: 'post',
      data: { refreshToken },
    });
  },

  logout: () => {
    return requestApi<void>({
      url: '/api/system/admin/logout',
      method: 'post',
    });
  },

  listAdmins: (params?: { status?: number; page?: number; size?: number }) => {
    return requestApi<PageResult<AdminDTO>>({
      url: '/api/system/admins',
      method: 'get',
      params,
    });
  },

  createAdmin: (data: CreateAdminRequest) => {
    return requestApi<number, CreateAdminRequest>({
      url: '/api/system/admins',
      method: 'post',
      data,
    });
  },

  updateAdmin: (id: number, data: { realName: string }) => {
    return requestApi<void, { realName: string }>({
      url: `/api/system/admins/${id}`,
      method: 'put',
      data,
    });
  },

  updateAdminPassword: (id: number, data: { password: string }) => {
    return requestApi<void, { password: string }>({
      url: `/api/system/admins/${id}/password`,
      method: 'put',
      data,
    });
  },

  updateAdminStatus: (id: number, status: number) => {
    return requestApi<void, { status: number }>({
      url: `/api/system/admins/${id}/status`,
      method: 'put',
      data: { status },
    });
  },

  batchUpdateAdminStatus: (adminIds: number[], status: number) => {
    return requestApi<void, { adminIds: number[]; status: number }>({
      url: '/api/system/admins/batch/status',
      method: 'put',
      data: { adminIds, status },
    });
  },

  batchDeleteAdmins: (adminIds: number[]) => {
    return requestApi<void, { adminIds: number[] }>({
      url: '/api/system/admins/batch',
      method: 'delete',
      data: { adminIds },
    });
  },

  assignRole: (data: AssignRoleRequest) => {
    return requestApi<void, AssignRoleRequest>({
      url: '/api/system/admin/roles',
      method: 'post',
      data,
    });
  },

  getMenuTree: () => {
    return requestApi<MenuTreeDTO[]>({
      url: '/api/system/menus/tree',
      method: 'get',
    });
  },

  createMenu: (data: CreateMenuRequest) => {
    return requestApi<number, CreateMenuRequest>({
      url: '/api/system/menus',
      method: 'post',
      data,
    });
  },

  createRole: (data: CreateRoleRequest) => {
    return requestApi<number, CreateRoleRequest>({
      url: '/api/system/roles',
      method: 'post',
      data,
    });
  },

  grantMenu: (data: GrantMenuRequest) => {
    return requestApi<void, GrantMenuRequest>({
      url: '/api/system/roles/menus',
      method: 'post',
      data,
    });
  },

  getDailyAnalysis: (date: string) => {
    return requestApi<DailyAnalysisDTO>({
      url: '/api/system/analysis/daily',
      method: 'get',
      params: { date },
    });
  },

  listUsers: (params?: { mobile?: string; status?: number; page?: number; size?: number }) => {
    return requestApi<PageResult<UserAdminDTO>>({
      url: '/api/system/users',
      method: 'get',
      params,
    });
  },

  getUserDetail: (id: string) => {
    return requestApi<UserDetailDTO>({
      url: `/api/system/users/${id}`,
      method: 'get',
    });
  },

  updateUserStatus: (id: string, status: number) => {
    return requestApi<void, { status: number }>({
      url: `/api/system/users/${id}/status`,
      method: 'put',
      data: { status },
    });
  },

  resetUserPassword: (id: string) => {
    return requestApi<{ newPassword: string }>({
      url: `/api/system/users/${id}/reset-password`,
      method: 'post',
    });
  },
};

export const contentApi = {
  timeline: (params?: { pageNo?: number; pageSize?: number }) => {
    return requestApi<unknown>({
      url: '/api/content/timeline',
      method: 'get',
      params,
    });
  },
};

export const socialApi = {
  followers: (params?: { pageNo?: number; pageSize?: number }) => {
    return requestApi<unknown>({
      url: '/api/social/followers',
      method: 'get',
      params,
    });
  },

  following: (params?: { pageNo?: number; pageSize?: number }) => {
    return requestApi<unknown>({
      url: '/api/social/following',
      method: 'get',
      params,
    });
  },

  getRecommendUsers: (params?: { pageNum?: number; pageSize?: number }) => {
    return requestApi<unknown>({
      url: '/api/recommends/users',
      method: 'get',
      params,
    });
  },
};

// ---------------------------
// 内容审核（管理端）
// ---------------------------
export const contentAdminApi = {
  listMovements: (params?: { page?: number; size?: number; userId?: string; status?: number }) => {
    return requestApi<PageResult<unknown>>({
      url: '/api/content/admin/movements',
      method: 'get',
      params,
    });
  },
  approveMovement: (id: number) => {
    return requestApi<void>({
      url: `/api/content/admin/movements/${id}/approve`,
      method: 'put',
    });
  },
  rejectMovement: (id: number, data?: { reason?: string }) => {
    return requestApi<void, { reason?: string } | undefined>({
      url: `/api/content/admin/movements/${id}/reject`,
      method: 'put',
      data,
    });
  },

  listVideos: (params?: { page?: number; size?: number; userId?: string; status?: number }) => {
    return requestApi<PageResult<unknown>>({
      url: '/api/content/admin/videos',
      method: 'get',
      params,
    });
  },
  approveVideo: (id: number) => {
    return requestApi<void>({
      url: `/api/content/admin/videos/${id}/approve`,
      method: 'put',
    });
  },
  rejectVideo: (id: number, data?: { reason?: string }) => {
    return requestApi<void, { reason?: string } | undefined>({
      url: `/api/content/admin/videos/${id}/reject`,
      method: 'put',
      data,
    });
  },

  listReports: (params?: { page?: number; size?: number; status?: number; keyword?: string }) => {
    return requestApi<PageResult<unknown>>({
      url: '/api/content/admin/reports',
      method: 'get',
      params,
    });
  },
  handleReport: (id: number, data?: { action?: string; remark?: string }) => {
    return requestApi<void, { action?: string; remark?: string } | undefined>({
      url: `/api/content/admin/reports/${id}/handle`,
      method: 'put',
      data,
    });
  },
  ignoreReport: (id: number, data?: { remark?: string }) => {
    return requestApi<void, { remark?: string } | undefined>({
      url: `/api/content/admin/reports/${id}/ignore`,
      method: 'put',
      data,
    });
  },
};

// ---------------------------
// 社交管理（管理端）
// ---------------------------
export const socialAdminApi = {
  listMatches: (params?: { page?: number; size?: number; userId?: string }) => {
    return requestApi<PageResult<unknown>>({
      url: '/api/social/admin/matches',
      method: 'get',
      params,
    });
  },
  listFriendships: (params?: { page?: number; size?: number; userId?: string }) => {
    return requestApi<PageResult<unknown>>({
      url: '/api/social/admin/friendships',
      method: 'get',
      params,
    });
  },
  blockFriendship: (id: number) => {
    return requestApi<void>({
      url: `/api/social/admin/friendships/${id}/block`,
      method: 'put',
    });
  },
  deleteFriendship: (id: number) => {
    return requestApi<void>({
      url: `/api/social/admin/friendships/${id}`,
      method: 'delete',
    });
  },
};

// ---------------------------
// 互动管理（管理端）
// ---------------------------
export const interactionAdminApi = {
  listComments: (params?: { page?: number; size?: number; keyword?: string; status?: number }) => {
    return requestApi<PageResult<unknown>>({
      url: '/api/interactions/admin/comments',
      method: 'get',
      params,
    });
  },
  deleteComment: (id: number) => {
    return requestApi<void>({
      url: `/api/interactions/admin/comments/${id}`,
      method: 'delete',
    });
  },
};

// ---------------------------
// 运维监控（管理端）
// ---------------------------
export const monitorApi = {
  listServices: () => {
    return requestApi<unknown[]>({
      url: '/api/monitor/services',
      method: 'get',
    });
  },
  listPerformance: (params?: { keyword?: string }) => {
    return requestApi<unknown[]>({
      url: '/api/monitor/performance',
      method: 'get',
      params,
    });
  },
  listAlertRules: (params?: { level?: string; enabled?: boolean; keyword?: string }) => {
    return requestApi<PageResult<unknown>>({
      url: '/api/monitor/alerts/rules',
      method: 'get',
      params,
    });
  },
  createAlertRule: (data: unknown) => {
    return requestApi<number, unknown>({
      url: '/api/monitor/alerts/rules',
      method: 'post',
      data,
    });
  },
  updateAlertRule: (id: number, data: unknown) => {
    return requestApi<void, unknown>({
      url: `/api/monitor/alerts/rules/${id}`,
      method: 'put',
      data,
    });
  },
  deleteAlertRule: (id: number) => {
    return requestApi<void>({
      url: `/api/monitor/alerts/rules/${id}`,
      method: 'delete',
    });
  },
  listAlertEvents: (params?: { level?: string; status?: string; keyword?: string; page?: number; size?: number }) => {
    return requestApi<PageResult<unknown>>({
      url: '/api/monitor/alerts/events',
      method: 'get',
      params,
    });
  },
  ackAlertEvent: (id: string) => {
    return requestApi<void>({
      url: `/api/monitor/alerts/events/${id}/ack`,
      method: 'put',
    });
  },
  resolveAlertEvent: (id: string) => {
    return requestApi<void>({
      url: `/api/monitor/alerts/events/${id}/resolve`,
      method: 'put',
    });
  },
};

// ---------------------------
// 消息推送（管理端）
// ---------------------------
export const messageApi = {
  listNotices: (params?: NoticeQueryParams) => {
    return requestApi<PageResult<NoticeDTO>>({
      url: '/api/message/admin/notices',
      method: 'get',
      params,
    });
  },

  sendNotice: (data: SendNoticeRequest) => {
    return requestApi<number, SendNoticeRequest>({
      url: '/api/message/admin/notices',
      method: 'post',
      data,
    });
  },

  deleteNotice: (id: number) => {
    return requestApi<void>({
      url: `/api/message/admin/notices/${id}`,
      method: 'delete',
    });
  },

  listTemplates: (params?: MessageTemplateQueryParams) => {
    return requestApi<PageResult<MessageTemplateDTO>>({
      url: '/api/message/admin/templates',
      method: 'get',
      params,
    });
  },

  createTemplate: (data: Omit<MessageTemplateDTO, 'id' | 'createdAt' | 'updatedAt'>) => {
    return requestApi<number, Omit<MessageTemplateDTO, 'id' | 'createdAt' | 'updatedAt'>>({
      url: '/api/message/admin/templates',
      method: 'post',
      data,
    });
  },

  updateTemplate: (id: number, data: Partial<Omit<MessageTemplateDTO, 'id' | 'createdAt' | 'updatedAt'>>) => {
    return requestApi<void, Partial<Omit<MessageTemplateDTO, 'id' | 'createdAt' | 'updatedAt'>>>({
      url: `/api/message/admin/templates/${id}`,
      method: 'put',
      data,
    });
  },

  listAnnouncements: (params?: AnnouncementQueryParams) => {
    return requestApi<PageResult<AnnouncementDTO>>({
      url: '/api/message/admin/announcements',
      method: 'get',
      params,
    });
  },

  createAnnouncement: (data: Omit<AnnouncementDTO, 'id' | 'createdAt' | 'updatedAt' | 'publishedAt' | 'publisherName'>) => {
    return requestApi<number, Omit<AnnouncementDTO, 'id' | 'createdAt' | 'updatedAt' | 'publishedAt' | 'publisherName'>>({
      url: '/api/message/admin/announcements',
      method: 'post',
      data,
    });
  },

  updateAnnouncement: (
    id: number,
    data: Partial<Omit<AnnouncementDTO, 'id' | 'createdAt' | 'updatedAt' | 'publishedAt' | 'publisherName'>>
  ) => {
    return requestApi<void, Partial<Omit<AnnouncementDTO, 'id' | 'createdAt' | 'updatedAt' | 'publishedAt' | 'publisherName'>>>({
      url: `/api/message/admin/announcements/${id}`,
      method: 'put',
      data,
    });
  },

  publishAnnouncement: (id: number) => {
    return requestApi<void>({
      url: `/api/message/admin/announcements/${id}/publish`,
      method: 'put',
    });
  },

  offlineAnnouncement: (id: number) => {
    return requestApi<void>({
      url: `/api/message/admin/announcements/${id}/offline`,
      method: 'put',
    });
  },

  toggleAnnouncementTop: (id: number, isTop: boolean) => {
    return requestApi<void, { isTop: boolean }>({
      url: `/api/message/admin/announcements/${id}/top`,
      method: 'put',
      data: { isTop },
    });
  },

  listRecords: (params?: PushRecordQueryParams) => {
    return requestApi<PageResult<PushRecordDTO>>({
      url: '/api/message/admin/records',
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
    return requestApi<PageResult<OperationLogDTO>>({
      url: '/api/system/logs/operation',
      method: 'get',
      params,
    });
  },

  getLoginLogs: (params?: LogQueryParams) => {
    return requestApi<PageResult<LoginLogDTO>>({
      url: '/api/system/logs/login',
      method: 'get',
      params,
    });
  },

  getErrorLogs: (params?: LogQueryParams & { level?: string; service?: string; handled?: boolean }) => {
    return requestApi<PageResult<ErrorLogDTO>>({
      url: '/api/system/logs/error',
      method: 'get',
      params,
    });
  },

  markErrorHandled: (id: string, handled = true) => {
    return requestApi<void, { handled: boolean }>({
      url: `/api/system/logs/error/${id}/handled`,
      method: 'put',
      data: { handled },
    });
  },

  getApiStats: (params?: LogQueryParams) => {
    return requestApi<PageResult<ApiStatDTO>>({
      url: '/api/system/logs/api',
      method: 'get',
      params,
    });
  },

  getSlowSql: (params?: LogQueryParams) => {
    return requestApi<PageResult<SlowSqlDTO>>({
      url: '/api/system/logs/slow-sql',
      method: 'get',
      params,
    });
  },

  /**
   * 管理后台接口访问日志（来自 quadra-system）
   */
  getRequestLogs: (params?: RequestLogQueryParams) => {
    return requestApi<PageResult<RequestLogDTO>>({
      url: '/api/system/logs/requests',
      method: 'get',
      params,
    });
  },
};
