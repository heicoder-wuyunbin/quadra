package com.quadra.recommend.application.port.in.dto;

import java.math.BigDecimal;

/**
 * 推荐内容DTO
 */
public record RecommendContentDTO(
    Long id,
    Long targetId,
    String targetType,
    BigDecimal score
) {}
