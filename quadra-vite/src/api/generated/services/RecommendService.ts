/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { RecordActionRequest } from '../models/RecordActionRequest';
import type { ResultLong } from '../models/ResultLong';
import type { ResultPageResultRecommendContentDTO } from '../models/ResultPageResultRecommendContentDTO';
import type { ResultPageResultRecommendUserDTO } from '../models/ResultPageResultRecommendUserDTO';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class RecommendService {
    /**
     * 记录用户行为
     * 记录用户对目标的行为（VIEW/LIKE/SKIP/DISLIKE）
     * @param requestBody
     * @returns ResultLong OK
     * @throws ApiError
     */
    public static recordAction(
        requestBody: RecordActionRequest,
    ): CancelablePromise<ResultLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/recommends/actions',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 获取今日推荐用户
     * 获取今日推荐用户列表，按分数倒序排列
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @returns ResultPageResultRecommendUserDTO OK
     * @throws ApiError
     */
    public static getTodayRecommendUsers(
        pageNum: number = 1,
        pageSize: number = 10,
    ): CancelablePromise<ResultPageResultRecommendUserDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/recommends/users',
            query: {
                'pageNum': pageNum,
                'pageSize': pageSize,
            },
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 获取推荐内容
     * 获取推荐内容列表，按分数倒序排列
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @returns ResultPageResultRecommendContentDTO OK
     * @throws ApiError
     */
    public static getRecommendContents(
        pageNum: number = 1,
        pageSize: number = 10,
    ): CancelablePromise<ResultPageResultRecommendContentDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/recommends/contents',
            query: {
                'pageNum': pageNum,
                'pageSize': pageSize,
            },
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
}
