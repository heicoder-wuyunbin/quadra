package com.quadra.recommend.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 推荐用户结果 DO
 */
@Data
@TableName("recommend_user")
public class RecommendUserDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long userId;
    
    private Long recommendTargetId;
    
    private BigDecimal score;
    
    private LocalDate recommendDate;
    
    @Version
    private Integer version;
    
    private LocalDateTime createdAt;
}
