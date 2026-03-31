package com.quadra.system.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_admin_role")
public class SysAdminRoleDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long adminId;
    
    private Long roleId;
    
    private LocalDateTime createdAt;
}
