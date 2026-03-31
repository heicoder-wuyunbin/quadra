/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 更新用户偏好设置请求参数
 */
export type UpdateSettingRequest = {
    /**
     * 点赞通知: 0-关闭, 1-开启
     */
    likeNotification?: number;
    /**
     * 评论通知: 0-关闭, 1-开启
     */
    commentNotification?: number;
    /**
     * 系统通知: 0-关闭, 1-开启
     */
    systemNotification?: number;
};

