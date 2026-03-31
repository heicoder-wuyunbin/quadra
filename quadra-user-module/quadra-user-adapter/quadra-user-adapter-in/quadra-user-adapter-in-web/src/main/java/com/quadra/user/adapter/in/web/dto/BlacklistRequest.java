package com.quadra.user.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "黑名单操作请求")
public record BlacklistRequest(
    @Schema(description = "目标用户ID")
    Long targetUserId
) {}
