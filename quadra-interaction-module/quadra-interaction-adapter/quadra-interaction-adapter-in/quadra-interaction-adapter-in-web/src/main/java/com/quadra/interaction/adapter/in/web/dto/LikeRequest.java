package com.quadra.interaction.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 点赞请求
 */
public record LikeRequest(
    @NotNull(message = "目标类型不能为空")
    String targetType,
    
    @NotNull(message = "目标ID不能为空")
    Long targetId
) {}
