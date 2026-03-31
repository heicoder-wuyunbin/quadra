package com.quadra.content.application.port.in.dto;

import java.time.LocalDateTime;

/**
 * 时间线项DTO
 */
public record TimelineItemDTO(
    Long id,
    Long movementId,
    Long authorId,
    String authorNickname,
    String authorAvatar,
    String textContent,
    String medias,  // JSON string
    Integer likeCount,
    Integer commentCount,
    LocalDateTime createdAt
) {}
