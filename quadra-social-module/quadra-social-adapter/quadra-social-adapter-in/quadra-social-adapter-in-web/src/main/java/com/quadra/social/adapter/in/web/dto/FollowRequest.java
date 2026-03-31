package com.quadra.social.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "关注请求参数")
public record FollowRequest(
    @Schema(description = "目标用户ID", example = "1234567890")
    Long targetUserId
) {}
