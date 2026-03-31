package com.quadra.recommend.application.port.in.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 推荐用户DTO
 */
public record RecommendUserDTO(
    Long id,
    Long recommendTargetId,
    BigDecimal score,
    LocalDate recommendDate
) {}
