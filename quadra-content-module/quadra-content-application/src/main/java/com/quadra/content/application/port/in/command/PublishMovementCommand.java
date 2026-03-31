package com.quadra.content.application.port.in.command;

import java.math.BigDecimal;
import java.util.List;

/**
 * 发布图文动态指令
 */
public record PublishMovementCommand(
    Long userId,
    String textContent,
    List<MediaInfo> medias,
    BigDecimal longitude,
    BigDecimal latitude,
    String locationName,
    Integer state
) {
    public record MediaInfo(
        String type,
        String url,
        String thumbnail,
        Integer width,
        Integer height
    ) {}
}
