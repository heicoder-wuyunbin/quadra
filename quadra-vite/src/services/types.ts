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
  id: number;
  mobile: string;
  nickname?: string;
  gender?: number;
  city?: string;
  status: number;
  createdAt: string;
}

export interface UserDetailDTO {
  id: number;
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
