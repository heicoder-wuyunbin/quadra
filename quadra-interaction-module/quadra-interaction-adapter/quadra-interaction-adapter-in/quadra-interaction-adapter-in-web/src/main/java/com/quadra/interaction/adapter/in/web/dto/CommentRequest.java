package com.quadra.interaction.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 评论请求
 */
public record CommentRequest(
    @NotNull(message = "目标类型不能为空")
    String targetType,
    
    @NotNull(message = "目标ID不能为空")
    Long targetId,
    
    @NotNull(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容不能超过500字")
    String content,
    
    Long replyToId
) {}
