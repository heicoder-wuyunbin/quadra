/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { PublishMovementRequest } from '../models/PublishMovementRequest';
import type { PublishVideoRequest } from '../models/PublishVideoRequest';
import type { ResultLong } from '../models/ResultLong';
import type { ResultPageResultTimelineItemDTO } from '../models/ResultPageResultTimelineItemDTO';
import type { ResultVoid } from '../models/ResultVoid';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class ContentService {
    /**
     * 发布短视频
     * 发布短视频
     * @param requestBody
     * @param xUserId 用户ID
     * @returns ResultLong OK
     * @throws ApiError
     */
    public static publishVideo(
        requestBody: PublishVideoRequest,
        xUserId?: number,
    ): CancelablePromise<ResultLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/content/videos',
            headers: {
                'X-User-Id': xUserId,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 发布图文动态
     * 发布图文动态，文本或媒体至少一项非空
     * @param requestBody
     * @param xUserId 用户ID
     * @returns ResultLong OK
     * @throws ApiError
     */
    public static publishMovement(
        requestBody: PublishMovementRequest,
        xUserId?: number,
    ): CancelablePromise<ResultLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/content/movements',
            headers: {
                'X-User-Id': xUserId,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 拉取时间线
     * 拉取用户的时间线Feed
     * @param xUserId 用户ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @returns ResultPageResultTimelineItemDTO OK
     * @throws ApiError
     */
    public static pullTimeline(
        xUserId?: number,
        pageNo: number = 1,
        pageSize: number = 20,
    ): CancelablePromise<ResultPageResultTimelineItemDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/content/timeline',
            headers: {
                'X-User-Id': xUserId,
            },
            query: {
                'pageNo': pageNo,
                'pageSize': pageSize,
            },
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 删除图文动态
     * 逻辑删除图文动态
     * @param id 动态ID
     * @param xUserId 用户ID
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public static deleteMovement(
        id: number,
        xUserId?: number,
    ): CancelablePromise<ResultVoid> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/content/movements/{id}',
            path: {
                'id': id,
            },
            headers: {
                'X-User-Id': xUserId,
            },
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
}
