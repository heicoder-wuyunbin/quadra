/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BlacklistRequest } from '../models/BlacklistRequest';
import type { ResultPageResultBlacklistItemDTO } from '../models/ResultPageResultBlacklistItemDTO';
import type { ResultVoid } from '../models/ResultVoid';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class BlacklistService {
    /**
     * 分页查询我的黑名单
     * @param pageNo
     * @param pageSize
     * @returns ResultPageResultBlacklistItemDTO OK
     * @throws ApiError
     */
    public static listMyBlacklist(
        pageNo: number = 1,
        pageSize: number = 10,
    ): CancelablePromise<ResultPageResultBlacklistItemDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/blacklists',
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
     * 添加黑名单
     * 拉黑目标用户
     * @param requestBody
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public static addBlacklist(
        requestBody: BlacklistRequest,
    ): CancelablePromise<ResultVoid> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/blacklists',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 移除黑名单
     * 取消拉黑目标用户
     * @param requestBody
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public static removeBlacklist(
        requestBody: BlacklistRequest,
    ): CancelablePromise<ResultVoid> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/v1/blacklists',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
}
