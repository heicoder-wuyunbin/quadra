package com.quadra.recommend.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 推荐内容结果 DO
 */
@Data
@TableName("recommend_content")
public class RecommendContentDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long userId;
    
    private Long targetId;
    
    private String targetType;
    
    private BigDecimal score;
    
    @Version
    private Integer version;
    
    private LocalDateTime createdAt;
}
