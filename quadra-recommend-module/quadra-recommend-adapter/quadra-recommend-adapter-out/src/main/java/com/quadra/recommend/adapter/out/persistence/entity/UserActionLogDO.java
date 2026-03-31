package com.quadra.recommend.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户行为日志 DO
 */
@Data
@TableName("user_action_log")
public class UserActionLogDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long userId;
    
    private Long targetId;
    
    private String targetType;
    
    private String actionType;
    
    private BigDecimal weight;
    
    @Version
    private Integer version;
    
    private LocalDateTime createdAt;
}
