export interface AdminLoginRequest {
  username: string;
  password: string;
}

export interface ApiResult<T> {
  success: boolean;
  code: number;
  message: string;
  data: T;
  timestamp: number;
  requestId: string;
}

export interface AdminLoginResultDTO {
  adminId: number;
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
  username?: string;
  realName?: string;
}

export interface AdminDTO {
  adminId: number;
  username: string;
  realName: string;
  status: number;
  createdAt: string;
}

export interface CreateAdminRequest {
  username: string;
  password: string;
  realName: string;
}

export interface CreateRoleRequest {
  roleCode: string;
  roleName: string;
  description?: string | null;
}

export interface AssignRoleRequest {
  adminId: number;
  roleIds: number[];
}

export interface CreateMenuRequest {
  parentId?: number | null;
  menuName: string;
  menuType: 'MENU' | 'BUTTON' | 'API';
  permissionCode?: string | null;
  path?: string | null;
  icon?: string | null;
  sortOrder?: number | null;
}

export interface GrantMenuRequest {
  roleId: number;
  menuIds: number[];
}

export interface MenuTreeDTO {
  menuId: number;
  menuName: string;
  menuType: string;
  parentId: number;
  path: string;
  icon: string;
  sortOrder: number;
  children: MenuTreeDTO[];
}

export interface DailyAnalysisDTO {
  date: string;
  newUsers: number;
  activeUsers: number;
  newContents: number;
  interactions: number;
  matches: number;
}

export interface FollowerDTO {
  userId: number;
  nickname: string;
  avatar: string;
  followedAt: string;
  isFollowing: boolean;
}

export interface RecommendUserDTO {
  userId: number;
  nickname: string;
  avatar: string;
  age: number;
  gender: string;
  distance: number;
  score: number;
  tags: string[];
}

export interface PageResult<T> {
  total: number;
  pageNo?: number;
  pageSize?: number;
  list?: T[];
  records?: T[];
  current?: number;
  size?: number;
  pages?: number;
}

export interface UserAdminDTO {
  id: string;
  mobile: string;
  nickname?: string;
  gender?: number;
  city?: string;
  status: number;
  createdAt: string;
}

export interface UserDetailDTO {
  id: string;
  mobile: string;
  status: number;
  createdAt: string;
  updatedAt?: string;
  nickname?: string;
  avatar?: string;
  gender?: number;
  birthday?: string;
  city?: string;
  income?: string;
  profession?: string;
  marriage?: number;
  coverPic?: string;
  tags?: string;
  likeNotification?: number;
  commentNotification?: number;
  systemNotification?: number;
}

// ---------------------------
// 消息推送（管理端）
// ---------------------------
export type NoticeType = 'SYSTEM' | 'ACTIVITY' | 'REMINDER' | 'CUSTOM';
export type NoticeStatus = 'DRAFT' | 'SENT' | 'DELETED';
export type NoticePriority = 'LOW' | 'NORMAL' | 'HIGH' | 'URGENT';
export type NoticeTargetType = 'ALL' | 'USER' | 'GROUP';

export interface NoticeDTO {
  id: number;
  title: string;
  content: string;
  type: NoticeType;
  targetType: NoticeTargetType;
  targetIds?: number[];
  senderId: number;
  senderName: string;
  isRead?: boolean;
  readCount?: number;
  status: NoticeStatus;
  priority: NoticePriority;
  scheduledAt?: string;
  sentAt?: string;
  createdAt: string;
}

export interface NoticeQueryParams {
  page?: number;
  size?: number;
  type?: NoticeType;
  status?: NoticeStatus;
  priority?: NoticePriority;
  keyword?: string;
}

export interface SendNoticeRequest {
  title: string;
  content: string;
  type: NoticeType;
  targetType: NoticeTargetType;
  targetIds?: number[];
  priority: NoticePriority;
  scheduledAt?: string;
  templateId?: number;
}

export interface MessageTemplateDTO {
  id: number;
  name: string;
  description?: string;
  content: string;
  variables?: string[];
  createdAt: string;
  updatedAt?: string;
}

export interface MessageTemplateQueryParams {
  page?: number;
  size?: number;
  keyword?: string;
}

export type AnnouncementStatus = 'DRAFT' | 'PUBLISHED' | 'OFFLINE';

export interface AnnouncementDTO {
  id: number;
  title: string;
  content: string;
  status: AnnouncementStatus;
  isTop: boolean;
  publisherName?: string;
  publishedAt?: string;
  createdAt: string;
  updatedAt?: string;
}

export interface AnnouncementQueryParams {
  page?: number;
  size?: number;
  status?: AnnouncementStatus;
  keyword?: string;
}

export type PushChannel = 'IN_APP' | 'PUSH' | 'SMS' | 'EMAIL';
export type PushBizType = 'NOTICE' | 'ANNOUNCEMENT';
export type PushStatus = 'SENDING' | 'SUCCESS' | 'FAILED' | 'PARTIAL';

export interface PushRecordDTO {
  id: number;
  bizType: PushBizType;
  channel: PushChannel;
  title: string;
  targetCount: number;
  successCount: number;
  failCount: number;
  readCount: number;
  status: PushStatus;
  sentAt?: string;
  createdAt: string;
}

export interface PushRecordQueryParams {
  page?: number;
  size?: number;
  bizType?: PushBizType;
  status?: PushStatus;
  keyword?: string;
}

// ---------------------------
// 日志监控（管理端）
// ---------------------------
export interface LogQueryParams {
  page?: number;
  size?: number;
  startTime?: string;
  endTime?: string;
  keyword?: string;
}

export interface OperationLogDTO {
  id: string;
  adminId: number;
  adminName: string;
  module: string;
  action: string;
  targetId?: number;
  targetName?: string;
  requestParams: unknown;
  responseStatus: number;
  ip: string;
  userAgent: string;
  executeTime: number;
  createdAt: string;
}

export interface LoginLogDTO {
  id: string;
  adminId: number;
  adminName: string;
  ip: string;
  location?: string;
  userAgent: string;
  status: 'SUCCESS' | 'FAILED';
  reason?: string;
  createdAt: string;
}

export interface ErrorLogDTO {
  id: string;
  level: 'ERROR' | 'WARN' | 'INFO';
  service: string;
  message: string;
  stackTrace: string;
  userId?: number;
  requestId: string;
  url: string;
  params: unknown;
  handled?: boolean;
  handledBy?: string;
  handledAt?: string;
  createdAt: string;
}

export interface ApiStatDTO {
  id: string;
  method: string;
  path: string;
  count: number;
  avgTime: number;
  p95Time?: number;
  errorRate: number; // 0-1
  lastCalledAt: string;
}

export interface SlowSqlDTO {
  id: string;
  db?: string;
  sql: string;
  executeTime: number;
  rowsExamined?: number;
  explain?: string;
  suggestion?: string;
  createdAt: string;
}

/**
 * 管理后台接口访问日志（/v1/system/logs/requests）
 */
export interface RequestLogDTO {
  id: number;
  service: string;
  traceId?: string;
  adminId?: number;
  method: string;
  path: string;
  queryString?: string;
  statusCode: number;
  durationMs: number;
  ipAddress?: string;
  userAgent?: string;
  requestHeaders?: string; // JSON string（已脱敏）
  requestBody?: string;
  responseBody?: string;
  createdAt: string;
}

export interface RequestLogQueryParams {
  page?: number;
  size?: number;
  service?: string;
  adminId?: number;
  statusCode?: number;
  method?: string;
  pathKeyword?: string;
  traceId?: string;
  startTime?: string; // yyyy-MM-dd HH:mm:ss
  endTime?: string; // yyyy-MM-dd HH:mm:ss
}
