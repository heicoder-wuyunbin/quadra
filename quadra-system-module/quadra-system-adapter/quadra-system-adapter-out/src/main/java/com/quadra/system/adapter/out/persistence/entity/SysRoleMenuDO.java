package com.quadra.system.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_role_menu")
public class SysRoleMenuDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long roleId;
    
    private Long menuId;
    
    private LocalDateTime createdAt;
}
