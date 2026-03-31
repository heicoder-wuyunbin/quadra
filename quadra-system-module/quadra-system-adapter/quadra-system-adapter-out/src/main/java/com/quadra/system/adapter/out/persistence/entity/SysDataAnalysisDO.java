package com.quadra.system.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("sys_data_analysis")
public class SysDataAnalysisDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private LocalDate recordDate;
    
    private Integer numRegistered;
    
    private Integer numActive;
    
    private Integer numMovement;
    
    private Integer numMatched;
    
    @TableField("num_retention_1d")
    private Integer numRetention1d;
    
    @Version
    private Integer version;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
