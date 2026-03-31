package com.quadra.system.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class SysRoleDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String roleCode;
    
    private String roleName;
    
    private String description;
    
    private Integer status;
    
    @Version
    private Integer version;
    
    @TableLogic
    private Integer deleted;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
