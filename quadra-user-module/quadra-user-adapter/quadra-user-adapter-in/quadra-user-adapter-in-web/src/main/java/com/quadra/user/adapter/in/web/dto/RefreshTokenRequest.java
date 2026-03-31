package com.quadra.user.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "刷新令牌请求")
public record RefreshTokenRequest(
        @Schema(description = "刷新令牌")
        String refreshToken
) {
}
