package com.quadra.system.application.port.in.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserDetailDTO(
        Long id,
        String mobile,
        Integer status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String nickname,
        String avatar,
        Integer gender,
        LocalDate birthday,
        String city,
        String income,
        String profession,
        Integer marriage,
        String coverPic,
        String tags,
        Integer likeNotification,
        Integer commentNotification,
        Integer systemNotification
) {}
