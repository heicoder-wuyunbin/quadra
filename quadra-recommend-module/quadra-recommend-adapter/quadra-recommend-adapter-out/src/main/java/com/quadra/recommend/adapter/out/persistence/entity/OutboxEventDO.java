package com.quadra.recommend.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Outbox 事件 DO
 */
@Data
@TableName("outbox_event")
public class OutboxEventDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 事件ID
     */
    private String eventId;
    
    /**
     * 事件类型
     */
    private String eventType;
    
    /**
     * 聚合类型
     */
    private String aggregateType;
    
    /**
     * 聚合ID
     */
    private Long aggregateId;
    
    /**
     * 事件载荷 (JSON)
     */
    private String payload;
    
    /**
     * 状态：0-待投递, 1-成功, 2-重试, 3-失败
     */
    private Integer status;
    
    /**
     * 重试次数
     */
    private Integer retryCount;
    
    /**
     * 错误信息
     */
    private String errorMsg;
    
    /**
     * 下次重试时间
     */
    private LocalDateTime nextRetryTime;
    
    private LocalDateTime createdAt;
}
