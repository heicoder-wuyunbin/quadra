/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { QuestionRequest } from '../models/QuestionRequest';
import type { ResultLong } from '../models/ResultLong';
import type { ResultPageResultQuestionItemDTO } from '../models/ResultPageResultQuestionItemDTO';
import type { ResultVoid } from '../models/ResultVoid';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class QuestionService {
    /**
     * 更新破冰问题
     * 更新问题内容或排序
     * @param id 问题ID
     * @param requestBody
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public static updateQuestion(
        id: number,
        requestBody: QuestionRequest,
    ): CancelablePromise<ResultVoid> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/v1/questions/{id}',
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
     * 禁用/删除破冰问题
     * 逻辑删除
     * @param id 问题ID
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public static disableQuestion(
        id: number,
    ): CancelablePromise<ResultVoid> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/v1/questions/{id}',
            path: {
                'id': id,
            },
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * 分页查询我的破冰问题
     * @param pageNo
     * @param pageSize
     * @returns ResultPageResultQuestionItemDTO OK
     * @throws ApiError
     */
    public static listMyQuestions(
        pageNo: number = 1,
        pageSize: number = 10,
    ): CancelablePromise<ResultPageResultQuestionItemDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/questions',
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
     * 添加破冰问题
     * 每个用户最多添加3个
     * @param requestBody
     * @returns ResultLong OK
     * @throws ApiError
     */
    public static addQuestion(
        requestBody: QuestionRequest,
    ): CancelablePromise<ResultLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/questions',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
}
