import { contentApi } from './request';

export interface PublishMovementRequest {
  textContent: string;
  medias?: Array<{
    type: string;
    url: string;
    thumbnail: string;
    width: number;
    height: number;
  }>;
  longitude?: number;
  latitude?: number;
  locationName?: string;
  state?: number;
}

export interface TimelineItemDTO {
  id: number;
  userId: number;
  targetType: string;
  textContent: string;
  medias: string;
  likeCount: number;
  commentCount: number;
  shareCount: number;
  createdAt: string;
}

/**
 * 发布图文动态
 */
export const publishMovement = async (data: PublishMovementRequest) => {
  return contentApi.post('/api/content/movements', data);
};

/**
 * 删除图文动态
 */
export const deleteMovement = async (movementId: number) => {
  return contentApi.delete(`/api/content/movements/${movementId}`);
};

/**
 * 拉取我的时间线
 */
export const pullTimeline = async (pageNo = 1, pageSize = 20) => {
  return contentApi.get('/api/content/timeline', {
    params: { pageNo, pageSize },
  });
};

/**
 * 发布视频
 */
export const publishVideo = async (data: {
  videoUrl: string;
  coverUrl: string;
  duration: number;
  textContent?: string;
}) => {
  return contentApi.post('/api/content/videos', data);
};
