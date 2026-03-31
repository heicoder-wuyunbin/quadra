package com.quadra.recommend.adapter.in.web.common;

/**
 * 全局错误码枚举规范
 * 参考阿里开发手册，使用 5 位数字
 * 成功：20000
 * 客户端错误：40000 - 49999
 * 服务端错误：50000 - 59999
 */
public enum ResultCode {
    
    // 成功
    SUCCESS(20000, "操作成功"),
    
    // 客户端通用错误 (400xx)
    BAD_REQUEST(40000, "请求参数错误"),
    UNAUTHORIZED(40001, "未授权或登录已过期"),
    FORBIDDEN(40003, "无权限访问"),
    NOT_FOUND(40004, "请求资源不存在"),
    
    // 业务规则冲突 (401xx)
    INVALID_ACTION_TYPE(40101, "非法的行为类型"),
    INVALID_TARGET_TYPE(40102, "非法的目标类型"),
    
    // 服务端通用错误 (500xx)
    INTERNAL_SERVER_ERROR(50000, "系统内部错误，请稍后重试");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
