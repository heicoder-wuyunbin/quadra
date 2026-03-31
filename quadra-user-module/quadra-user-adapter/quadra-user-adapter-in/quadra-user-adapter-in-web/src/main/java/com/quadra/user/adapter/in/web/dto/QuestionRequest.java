package com.quadra.user.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "破冰问题请求")
public record QuestionRequest(
    @Schema(description = "问题内容")
    String question,
    
    @Schema(description = "排序号", example = "0")
    Integer sortOrder
) {}
