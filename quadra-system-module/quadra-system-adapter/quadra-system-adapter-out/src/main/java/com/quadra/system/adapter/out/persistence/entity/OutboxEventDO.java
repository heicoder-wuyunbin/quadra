package com.quadra.system.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("outbox_event")
public class OutboxEventDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String aggregateType;
    
    private Long aggregateId;
    
    private String eventType;
    
    private String payload;
    
    private Integer status;
    
    private Integer retryCount;
    
    private LocalDateTime nextRetryTime;
    
    private String errorMsg;
    
    @Version
    private Integer version;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
