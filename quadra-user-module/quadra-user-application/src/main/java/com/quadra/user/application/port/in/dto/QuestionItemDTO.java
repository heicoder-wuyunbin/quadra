package com.quadra.user.application.port.in.dto;

import java.time.LocalDateTime;

public record QuestionItemDTO(
        Long id,
        String question,
        Integer sortOrder,
        Integer status,
        LocalDateTime createTime
) {
}
