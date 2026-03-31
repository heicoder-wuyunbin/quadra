const API_BASE_URL = 'http://127.0.0.1:18080'
const SESSION_STORAGE_KEY = 'quadra-session'

export interface Result<T> {
  code: number
  message: string
  success: boolean
  data: T
  requestId: string
  timestamp: string
}

export interface PageResult<T> {
  total: number
  pageNo: number
  pageSize: number
  list: T[]
}

export interface Session {
  userId: number
  accessToken: string
  refreshToken: string
  expiresIn: number
}

export interface LoginRequest {
  mobile: string
  password: string
}

export interface UserProfileDTO {
  userId: number
  nickname?: string
  avatar?: string
  gender?: 'MALE' | 'FEMALE' | 'UNKNOWN'
  birthday?: string
  city?: string
  income?: string
  profession?: string
  marriage?: 'SINGLE' | 'MARRIED' | 'DIVORCED'
  coverPic?: string
  tags?: string[]
}

export interface UpdateProfileRequest {
  nickname?: string
  avatar?: string
  gender?: 'MALE' | 'FEMALE' | 'UNKNOWN'
  birthday?: string
  city?: string
  income?: string
  profession?: string
  marriage?: 'SINGLE' | 'MARRIED' | 'DIVORCED'
  coverPic?: string
  tags?: string[]
}

export interface UpdateSettingRequest {
  likeNotification?: boolean
  commentNotification?: boolean
  systemNotification?: boolean
}

export interface NotificationSettingDraft {
  likeNotification: boolean
  commentNotification: boolean
  systemNotification: boolean
}

export interface BlacklistItemDTO {
  userId: number
  nickname?: string
  avatar?: string
  blacklistedAt?: string
}

export interface QuestionItemDTO {
  questionId: number
  question: string
  sortOrder?: number
  createdAt?: string
}

type RequestMethod = 'GET' | 'POST' | 'PUT' | 'DELETE'

interface RequestOptions {
  url: string
  method?: RequestMethod
  data?: unknown
  auth?: boolean
}

export const defaultAvatar =
  'https://mmbiz.qpic.cn/mmbiz/icTdbqWNOwNRna42FI242Lcia07jQodd2FJGIYQfG0LAJGFxM4FbnQP6yfMxBgJ0F3YRqJCJ1aPAK2dQagdusBZg/0'

export const defaultNotificationSetting: NotificationSettingDraft = {
  likeNotification: true,
  commentNotification: true,
  systemNotification: true,
}

function buildUrl(url: string, data?: unknown): string {
  if (!data || typeof data !== 'object' || Object.keys(data).length === 0) {
    return `${API_BASE_URL}${url}`
  }

  const query = Object.keys(data)
    .filter((key) => {
      const value = (data as Record<string, unknown>)[key]
      return value !== undefined && value !== null && value !== ''
    })
    .map((key) => {
      const value = (data as Record<string, unknown>)[key]
      return `${encodeURIComponent(key)}=${encodeURIComponent(String(value))}`
    })
    .join('&')

  return query ? `${API_BASE_URL}${url}?${query}` : `${API_BASE_URL}${url}`
}

function request<T>(options: RequestOptions): Promise<T> {
  return new Promise((resolve, reject) => {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    }

    if (options.auth) {
      const session = getSession()
      if (!session) {
        reject(new Error('请先登录后再继续'))
        return
      }
      headers.Authorization = `Bearer ${session.accessToken}`
    }

    wx.request({
      url: options.method === 'GET' ? buildUrl(options.url, options.data) : `${API_BASE_URL}${options.url}`,
      method: options.method ?? 'GET',
      data:
        options.method === 'GET'
          ? undefined
          : (options.data as WechatMiniprogram.IAnyObject | string | ArrayBuffer | undefined),
      header: headers,
      success: (response) => {
        const payload = response.data as Result<T>
        if (response.statusCode >= 200 && response.statusCode < 300 && payload && payload.success) {
          resolve(payload.data)
          return
        }

        reject(new Error(payload?.message || `请求失败(${response.statusCode})`))
      },
      fail: (error) => {
        reject(new Error(error.errMsg || '网络连接失败，请稍后再试'))
      },
    })
  })
}

export function getErrorMessage(error: unknown): string {
  if (error instanceof Error) {
    return error.message
  }

  return '操作失败，请稍后再试'
}

export function saveSession(session: Session): void {
  wx.setStorageSync(SESSION_STORAGE_KEY, session)
}

export function getSession(): Session | null {
  const session = wx.getStorageSync(SESSION_STORAGE_KEY) as Session | null
  return session || null
}

export function clearSession(): void {
  wx.removeStorageSync(SESSION_STORAGE_KEY)
}

export function getNotificationSettingDraft(userId: number): NotificationSettingDraft {
  const draft = wx.getStorageSync(`quadra-setting-${userId}`) as NotificationSettingDraft | null
  return draft || { ...defaultNotificationSetting }
}

export function saveNotificationSettingDraft(
  userId: number,
  draft: NotificationSettingDraft,
): void {
  wx.setStorageSync(`quadra-setting-${userId}`, draft)
}

export function login(payload: LoginRequest): Promise<Session> {
  return request<Session>({
    url: '/v1/users/login',
    method: 'POST',
    data: payload,
  })
}

export function logout(): Promise<void> {
  return request<void>({
    url: '/v1/users/logout',
    method: 'POST',
    auth: true,
  })
}

export function getUserProfile(userId: number): Promise<UserProfileDTO> {
  return request<UserProfileDTO>({
    url: `/v1/users/${userId}/profile`,
    auth: true,
  })
}

export function updateUserProfile(
  userId: number,
  payload: UpdateProfileRequest,
): Promise<void> {
  return request<void>({
    url: `/v1/users/${userId}/profile`,
    method: 'PUT',
    data: payload,
    auth: true,
  })
}

export function updateUserSetting(
  userId: number,
  payload: UpdateSettingRequest,
): Promise<void> {
  return request<void>({
    url: `/v1/users/${userId}/setting`,
    method: 'PUT',
    data: payload,
    auth: true,
  })
}

export function listMyBlacklist(
  pageNo = 1,
  pageSize = 10,
): Promise<PageResult<BlacklistItemDTO>> {
  return request<PageResult<BlacklistItemDTO>>({
    url: '/v1/blacklists',
    data: { pageNo, pageSize },
    auth: true,
  })
}

export function addBlacklist(targetUserId: number): Promise<void> {
  return request<void>({
    url: '/v1/blacklists',
    method: 'POST',
    data: { targetUserId },
    auth: true,
  })
}

export function removeBlacklist(targetUserId: number): Promise<void> {
  return request<void>({
    url: '/v1/blacklists',
    method: 'DELETE',
    data: { targetUserId },
    auth: true,
  })
}

export function listMyQuestions(
  pageNo = 1,
  pageSize = 10,
): Promise<PageResult<QuestionItemDTO>> {
  return request<PageResult<QuestionItemDTO>>({
    url: '/v1/questions',
    data: { pageNo, pageSize },
    auth: true,
  })
}

export function addQuestion(question: string, sortOrder: number): Promise<number> {
  return request<number>({
    url: '/v1/questions',
    method: 'POST',
    data: { question, sortOrder },
    auth: true,
  })
}

export function updateQuestion(
  questionId: number,
  question: string,
  sortOrder: number,
): Promise<void> {
  return request<void>({
    url: `/v1/questions/${questionId}`,
    method: 'PUT',
    data: { question, sortOrder },
    auth: true,
  })
}

export function deleteQuestion(questionId: number): Promise<void> {
  return request<void>({
    url: `/v1/questions/${questionId}`,
    method: 'DELETE',
    auth: true,
  })
}

export function formatDateTime(value?: string): string {
  if (!value) {
    return '刚刚'
  }

  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return value
  }

  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hour = String(date.getHours()).padStart(2, '0')
  const minute = String(date.getMinutes()).padStart(2, '0')

  return `${year}-${month}-${day} ${hour}:${minute}`
}
