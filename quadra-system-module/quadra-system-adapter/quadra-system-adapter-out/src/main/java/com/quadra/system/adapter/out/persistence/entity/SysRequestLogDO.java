package com.quadra.system.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理后台接口访问日志（用于排查 401/404/耗时等问题）
 */
@Data
@TableName("sys_request_log")
public class SysRequestLogDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 来源服务（如 quadra-system / quadra-gateway）
     */
    private String service;

    /**
     * 链路ID（requestId/traceId）
     */
    private String traceId;

    /**
     * 管理员ID（若已鉴权通过）
     */
    private Long adminId;

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

    private LocalDateTime createdAt;
}

