/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CommentRequest } from '../models/CommentRequest';
import type { LikeRequest } from '../models/LikeRequest';
import type { ResultMapStringLong } from '../models/ResultMapStringLong';
import type { ResultPageResultCommentDTO } from '../models/ResultPageResultCommentDTO';
import type { ResultVoid } from '../models/ResultVoid';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class InteractionControllerService {
    /**
     * @param requestBody
     * @returns ResultMapStringLong OK
     * @throws ApiError
     */
    public static like(
        requestBody: LikeRequest,
    ): CancelablePromise<ResultMapStringLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/interactions/likes',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * @param targetType
     * @param targetId
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public static cancelLike(
        targetType: string,
        targetId: number,
    ): CancelablePromise<ResultVoid> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/v1/interactions/likes',
            query: {
                'targetType': targetType,
                'targetId': targetId,
            },
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * @param targetType
     * @param targetId
     * @param cursor
     * @param pageSize
     * @returns ResultPageResultCommentDTO OK
     * @throws ApiError
     */
    public static listComments(
        targetType: string,
        targetId: number,
        cursor?: number,
        pageSize: number = 10,
    ): CancelablePromise<ResultPageResultCommentDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/interactions/comments',
            query: {
                'targetType': targetType,
                'targetId': targetId,
                'cursor': cursor,
                'pageSize': pageSize,
            },
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * @param requestBody
     * @returns ResultMapStringLong OK
     * @throws ApiError
     */
    public static comment(
        requestBody: CommentRequest,
    ): CancelablePromise<ResultMapStringLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/interactions/comments',
            body: requestBody,
            mediaType: 'application/json',
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
    /**
     * @param commentId
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public static deleteComment(
        commentId: number,
    ): CancelablePromise<ResultVoid> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/v1/interactions/comments/{commentId}',
            path: {
                'commentId': commentId,
            },
            errors: {
                500: `Internal Server Error`,
            },
        });
    }
}
