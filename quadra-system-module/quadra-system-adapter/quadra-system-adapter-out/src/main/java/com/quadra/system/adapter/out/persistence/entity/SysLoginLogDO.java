package com.quadra.system.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_login_log")
public class SysLoginLogDO {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long adminId;
    private String adminName;
    private String ip;
    private String location;
    private String userAgent;
    private String status;
    private String reason;
    private LocalDateTime createdAt;
}
