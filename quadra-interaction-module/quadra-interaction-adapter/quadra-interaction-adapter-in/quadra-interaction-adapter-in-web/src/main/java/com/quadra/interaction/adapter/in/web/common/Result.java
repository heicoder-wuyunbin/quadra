package com.quadra.interaction.adapter.in.web.common;

import org.slf4j.MDC;

import java.time.Instant;

/**
 * 统一 API 响应包裹类
 * @param <T> 数据泛型
 */
public class Result<T> {
    
    private boolean success;
    private int code;
    private String message;
    private T data;
    private long timestamp;
    private String requestId;

    // 禁用公开无参构造，强制通过静态方法创建
    private Result() {
        this.timestamp = Instant.now().toEpochMilli();
        String traceId = MDC.get("traceId");
        this.requestId = traceId != null ? traceId : java.util.UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功响应（带数据）
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.success = true;
        result.code = ResultCode.SUCCESS.getCode();
        result.message = ResultCode.SUCCESS.getMessage();
        result.data = data;
        return result;
    }

    /**
     * 失败响应（使用标准枚举）
     */
    public static <T> Result<T> failure(ResultCode resultCode) {
        Result<T> result = new Result<>();
        result.success = false;
        result.code = resultCode.getCode();
        result.message = resultCode.getMessage();
        return result;
    }

    /**
     * 失败响应（自定义消息）
     */
    public static <T> Result<T> failure(ResultCode resultCode, String customMessage) {
        Result<T> result = new Result<>();
        result.success = false;
        result.code = resultCode.getCode();
        result.message = customMessage;
        return result;
    }

    // Getters
    public boolean isSuccess() { return success; }
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public long getTimestamp() { return timestamp; }
    public String getRequestId() { return requestId; }
}
