/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { MediaInfo } from './MediaInfo';
/**
 * 发布图文动态请求
 */
export type PublishMovementRequest = {
    /**
     * 文本内容
     */
    textContent?: string;
    /**
     * 媒体列表
     */
    medias?: Array<MediaInfo>;
    /**
     * 经度
     */
    longitude?: number;
    /**
     * 纬度
     */
    latitude?: number;
    /**
     * 位置名称
     */
    locationName?: string;
    /**
     * 审核状态：0-未审核，1-通过
     */
    state?: number;
};

