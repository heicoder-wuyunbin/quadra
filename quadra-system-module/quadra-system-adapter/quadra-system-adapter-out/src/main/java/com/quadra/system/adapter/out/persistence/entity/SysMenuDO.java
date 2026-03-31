package com.quadra.system.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_menu")
public class SysMenuDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long parentId;
    
    private String menuName;
    
    private Integer menuType;
    
    private String permissionCode;
    
    private String path;
    
    private String icon;
    
    private Integer sortOrder;
    
    private Integer status;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
