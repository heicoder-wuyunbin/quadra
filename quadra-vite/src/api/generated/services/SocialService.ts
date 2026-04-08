/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { FollowRequest } from '../models/FollowRequest';
import type { ResultMatchResultDTO } from '../models/ResultMatchResultDTO';
import type { ResultPageResultFollowerDTO } from '../models/ResultPageResultFollowerDTO';
import type { ResultVoid } from '../models/ResultVoid';
import type { SwipeRequest } from '../models/SwipeRequest';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class SocialService {
    /**
     * 滑动操作
     * 对用户进行 LIKE/DISLIKE 操作
     * @param requestBody
     * @returns ResultMatchResultDTO OK
     * @throws ApiError
     */
    public static swipe(
        requestBody: SwipeRequest,
    ): CancelablePromise<ResultMatchResultDTO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/social/swipes',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 关注用户
     * 关注指定用户
     * @param requestBody
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public static follow(
        requestBody: FollowRequest,
    ): CancelablePromise<ResultVoid> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/social/follows',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 获取关注列表
     * 分页查询当前用户关注的人列表
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @returns ResultPageResultFollowerDTO OK
     * @throws ApiError
     */
    public static getFollowing(
        pageNo: number = 1,
        pageSize: number = 20,
    ): CancelablePromise<ResultPageResultFollowerDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/social/following',
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
     * 获取粉丝列表
     * 分页查询当前用户的粉丝列表
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @returns ResultPageResultFollowerDTO OK
     * @throws ApiError
     */
    public static getFollowers(
        pageNo: number = 1,
        pageSize: number = 20,
    ): CancelablePromise<ResultPageResultFollowerDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/social/followers',
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
     * 取消关注
     * 取消关注指定用户
     * @param targetUserId 目标用户ID
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public static unfollow(
        targetUserId: number,
    ): CancelablePromise<ResultVoid> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/social/follows/{targetUserId}',
            path: {
                'targetUserId': targetUserId,
            },
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
}
