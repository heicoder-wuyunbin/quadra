package com.quadra.social.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "滑动请求参数")
public record SwipeRequest(
    @Schema(description = "目标用户ID", example = "1234567890")
    Long targetUserId,
    
    @Schema(description = "行为类型: LIKE-喜欢(右滑), DISLIKE-不喜欢(左滑)", example = "LIKE")
    String likeType
) {}
