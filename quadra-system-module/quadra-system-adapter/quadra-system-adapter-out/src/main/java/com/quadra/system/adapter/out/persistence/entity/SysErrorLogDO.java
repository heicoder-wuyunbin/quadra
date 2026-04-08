package com.quadra.system.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_error_log")
public class SysErrorLogDO {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private String level;
    private String service;
    private String message;
    private String stackTrace;
    private Long userId;
    private String requestId;
    private String url;
    private String params;
    private Boolean handled;
    private String handledBy;
    private LocalDateTime handledAt;
    private LocalDateTime createdAt;
}
