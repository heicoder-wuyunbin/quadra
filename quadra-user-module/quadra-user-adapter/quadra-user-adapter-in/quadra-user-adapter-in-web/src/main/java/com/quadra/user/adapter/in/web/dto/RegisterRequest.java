package com.quadra.user.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户注册请求参数")
public record RegisterRequest(
    @Schema(description = "手机号", example = "13800138000")
    String mobile,
    
    @Schema(description = "密码", example = "Password123")
    String password
) {}
