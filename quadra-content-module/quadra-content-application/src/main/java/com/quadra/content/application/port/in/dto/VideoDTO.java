package com.quadra.content.application.port.in.dto;

import java.time.LocalDateTime;

/**
 * 短视频DTO
 */
public record VideoDTO(
    Long id,
    Long userId,
    String textContent,
    String videoUrl,
    String coverUrl,
    Integer duration,
    Integer likeCount,
    Integer commentCount,
    LocalDateTime createdAt
) {}
