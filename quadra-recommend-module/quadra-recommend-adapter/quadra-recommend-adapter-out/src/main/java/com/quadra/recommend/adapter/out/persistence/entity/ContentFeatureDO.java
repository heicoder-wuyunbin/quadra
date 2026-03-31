package com.quadra.recommend.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 内容特征 DO
 */
@Data
@TableName("content_feature")
public class ContentFeatureDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long targetId;
    
    private String targetType;
    
    private Integer heatScore;
    
    /**
     * 内容标签特征 (JSON)
     */
    private String tagsSummary;
    
    /**
     * AI 稠密向量 (JSON)
     */
    private String aiEmbedding;
    
    @Version
    private Integer version;
    
    private LocalDateTime updatedAt;
}
