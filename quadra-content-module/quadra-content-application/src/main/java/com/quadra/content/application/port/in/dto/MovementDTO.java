package com.quadra.content.application.port.in.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 图文动态DTO
 */
public record MovementDTO(
    Long id,
    Long userId,
    String textContent,
    List<MediaDTO> medias,
    BigDecimal longitude,
    BigDecimal latitude,
    String locationName,
    Integer state,
    Integer likeCount,
    Integer commentCount,
    LocalDateTime createdAt
) {
    public record MediaDTO(
        String type,
        String url,
        String thumbnail,
        Integer width,
        Integer height
    ) {}
}
