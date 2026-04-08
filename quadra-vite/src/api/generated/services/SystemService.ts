/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AdminLoginRequest } from '../models/AdminLoginRequest';
import type { AssignRoleRequest } from '../models/AssignRoleRequest';
import type { CreateAdminRequest } from '../models/CreateAdminRequest';
import type { CreateMenuRequest } from '../models/CreateMenuRequest';
import type { CreateRoleRequest } from '../models/CreateRoleRequest';
import type { GrantMenuRequest } from '../models/GrantMenuRequest';
import type { RefreshTokenRequest } from '../models/RefreshTokenRequest';
import type { ResultAdminLoginResultDTO } from '../models/ResultAdminLoginResultDTO';
import type { ResultAdminTokenResultDTO } from '../models/ResultAdminTokenResultDTO';
import type { ResultDailyAnalysisDTO } from '../models/ResultDailyAnalysisDTO';
import type { ResultListMenuTreeDTO } from '../models/ResultListMenuTreeDTO';
import type { ResultLong } from '../models/ResultLong';
import type { ResultPageResultAdminDTO } from '../models/ResultPageResultAdminDTO';
import type { ResultVoid } from '../models/ResultVoid';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class SystemService {
    /**
     * 创建角色
     * 创建新的角色
     * @param requestBody
     * @returns ResultLong OK
     * @throws ApiError
     */
    public static createRole(
        requestBody: CreateRoleRequest,
    ): CancelablePromise<ResultLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/system/roles',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 授予菜单权限
     * 为角色授予菜单权限
     * @param requestBody
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public static grantMenu(
        requestBody: GrantMenuRequest,
    ): CancelablePromise<ResultVoid> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/system/roles/menus',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 创建菜单
     * 创建新的菜单或权限点
     * @param requestBody
     * @returns ResultLong OK
     * @throws ApiError
     */
    public static createMenu(
        requestBody: CreateMenuRequest,
    ): CancelablePromise<ResultLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/system/menus',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 获取管理员列表
     * 分页查询管理员列表
     * @param status 状态筛选
     * @param page 页码
     * @param size 每页数量
     * @returns ResultPageResultAdminDTO OK
     * @throws ApiError
     */
    public static listAdmins(
        status?: number,
        page: number = 1,
        size: number = 10,
    ): CancelablePromise<ResultPageResultAdminDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/admins',
            query: {
                'status': status,
                'page': page,
                'size': size,
            },
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 创建管理员
     * 创建新的管理员账号
     * @param requestBody
     * @returns ResultLong OK
     * @throws ApiError
     */
    public static createAdmin(
        requestBody: CreateAdminRequest,
    ): CancelablePromise<ResultLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/system/admins',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 分配角色
     * 为管理员分配角色
     * @param requestBody
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public static assignRole(
        requestBody: AssignRoleRequest,
    ): CancelablePromise<ResultVoid> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/system/admin/roles',
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
     * @returns ResultAdminTokenResultDTO OK
     * @throws ApiError
     */
    public static refresh(
        requestBody: RefreshTokenRequest,
    ): CancelablePromise<ResultAdminTokenResultDTO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/system/admin/refresh',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 管理员登出
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
            url: '/api/system/admin/logout',
            headers: {
                'Authorization': authorization,
            },
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 管理员登录
     * 使用用户名和密码进行登录，返回双Token
     * @param requestBody
     * @returns ResultAdminLoginResultDTO OK
     * @throws ApiError
     */
    public static login(
        requestBody: AdminLoginRequest,
    ): CancelablePromise<ResultAdminLoginResultDTO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/system/admin/login',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 获取菜单树
     * 获取完整的菜单树结构
     * @returns ResultListMenuTreeDTO OK
     * @throws ApiError
     */
    public static getMenuTree(): CancelablePromise<ResultListMenuTreeDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/menus/tree',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 获取每日分析数据
     * 按日期查询每日统计分析数据
     * @param date 日期
     * @returns ResultDailyAnalysisDTO OK
     * @throws ApiError
     */
    public static getDailyAnalysis(
        date: string,
    ): CancelablePromise<ResultDailyAnalysisDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/system/analysis/daily',
            query: {
                'date': date,
            },
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
}
