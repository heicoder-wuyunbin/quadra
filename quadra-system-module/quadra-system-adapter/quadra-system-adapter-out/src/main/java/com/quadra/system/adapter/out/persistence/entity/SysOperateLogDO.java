package com.quadra.system.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_operate_log")
public class SysOperateLogDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long adminId;
    
    private String adminName;
    
    private String module;
    
    private String action;
    
    private Long targetId;
    
    private String ipAddress;

    private String userAgent;

    private String requestParams;

    private Integer responseStatus;

    private Integer executeTime;
    
    private LocalDateTime createdAt;
}
