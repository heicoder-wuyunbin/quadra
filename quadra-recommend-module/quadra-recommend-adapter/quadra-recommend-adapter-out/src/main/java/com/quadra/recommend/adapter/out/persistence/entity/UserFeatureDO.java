package com.quadra.recommend.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户特征 DO
 */
@Data
@TableName("user_feature")
public class UserFeatureDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long userId;
    
    private Integer activeScore;
    
    /**
     * 统计聚合后的偏好标签分布 (JSON)
     */
    private String tagsSummary;
    
    /**
     * AI 大模型生成的稠密向量 (JSON)
     */
    private String aiEmbedding;
    
    @Version
    private Integer version;
    
    private LocalDateTime updatedAt;
}
