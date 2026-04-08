/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { RefreshTokenRequest } from '../models/RefreshTokenRequest';
import type { RegisterRequest } from '../models/RegisterRequest';
import type { ResultLoginResultDTO } from '../models/ResultLoginResultDTO';
import type { ResultLong } from '../models/ResultLong';
import type { ResultUserProfileDTO } from '../models/ResultUserProfileDTO';
import type { ResultVoid } from '../models/ResultVoid';
import type { UpdateProfileRequest } from '../models/UpdateProfileRequest';
import type { UpdateSettingRequest } from '../models/UpdateSettingRequest';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class UserService {
    /**
     * 更新偏好设置
     * 根据用户ID更新偏好设置，仅支持增量更新
     * @param id 用户ID
     * @param requestBody
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public static updateSetting(
        id: number,
        requestBody: UpdateSettingRequest,
    ): CancelablePromise<ResultVoid> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/users/{id}/setting',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 获取用户资料
     * 根据用户ID获取基础资料及扩展标签
     * @param id 用户ID
     * @returns ResultUserProfileDTO OK
     * @throws ApiError
     */
    public static getProfile(
        id: number,
    ): CancelablePromise<ResultUserProfileDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/users/{id}/profile',
            path: {
                'id': id,
            },
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 更新用户资料
     * 根据用户ID更新资料，仅支持增量更新（传null的字段不更新）
     * @param id 用户ID
     * @param requestBody
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public static updateProfile(
        id: number,
        requestBody: UpdateProfileRequest,
    ): CancelablePromise<ResultVoid> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/users/{id}/profile',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 用户注册
     * 使用手机号和密码进行注册
     * @param requestBody
     * @returns ResultLong OK
     * @throws ApiError
     */
    public static register(
        requestBody: RegisterRequest,
    ): CancelablePromise<ResultLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/users/register',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 刷新令牌
     * 使用refresh token换取新token对
     * @param requestBody
     * @returns ResultLoginResultDTO OK
     * @throws ApiError
     */
    public static refresh(
        requestBody: RefreshTokenRequest,
    ): CancelablePromise<ResultLoginResultDTO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/users/refresh',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 用户登出
     * 将当前access token加入登出黑名单
     * @param authorization
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public static logout(
        authorization: string,
    ): CancelablePromise<ResultVoid> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/users/logout',
            headers: {
                'Authorization': authorization,
            },
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 用户登录
     * 使用手机号和密码进行登录，返回双Token
     * @param requestBody
     * @returns ResultLoginResultDTO OK
     * @throws ApiError
     */
    public static login(
        requestBody: RegisterRequest,
    ): CancelablePromise<ResultLoginResultDTO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/users/login',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
}
