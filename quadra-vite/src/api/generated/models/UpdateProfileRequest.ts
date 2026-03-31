/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 更新用户资料请求参数
 */
export type UpdateProfileRequest = {
    /**
     * 昵称
     */
    nickname?: string;
    /**
     * 头像URL
     */
    avatar?: string;
    /**
     * 性别: 0-未知, 1-男, 2-女
     */
    gender?: number;
    /**
     * 生日
     */
    birthday?: string;
    /**
     * 城市
     */
    city?: string;
    /**
     * 收入
     */
    income?: string;
    /**
     * 职业
     */
    profession?: string;
    /**
     * 婚姻状况: 0-未婚, 1-离异, 2-丧偶
     */
    marriage?: number;
    /**
     * 封面图
     */
    coverPic?: string;
    /**
     * 动态标签(JSON格式)
     */
    tags?: Record<string, Record<string, any>>;
};

