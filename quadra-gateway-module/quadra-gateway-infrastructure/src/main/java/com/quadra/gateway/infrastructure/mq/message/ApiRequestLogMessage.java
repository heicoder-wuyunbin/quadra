package com.quadra.gateway.infrastructure.mq.message;

import lombok.Data;

import java.io.Serializable;

/**
 * 网关侧采集的接口请求日志消息（用于异步落库）
 */
@Data
public class ApiRequestLogMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String traceId;
    private String service;

    private String method;
    private String path;
    private String queryString;

    private Integer statusCode;
    private Integer durationMs;

    private String ipAddress;
    private String userAgent;

    /**
     * JSON 字符串（已脱敏）
     */
    private String requestHeaders;

    private String requestBody;
    private String responseBody;

    private String errorMessage;
    private String errorStack;

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    private String createdAt;
}

