package com.quadra.gateway.infrastructure.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 管理员登录事件载荷
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminLoginEventPayload implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 管理员 ID
     */
    private Long adminId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 登录 IP
     */
    private String loginIp;
}
