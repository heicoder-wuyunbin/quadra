package com.quadra.system.adapter.out.persistence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_admin")
public class SysAdminDO {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String realName;
    
    private String avatar;
    
    private Integer status;
    
    @Version
    private Integer version;
    
    @TableLogic
    private Integer deleted;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
