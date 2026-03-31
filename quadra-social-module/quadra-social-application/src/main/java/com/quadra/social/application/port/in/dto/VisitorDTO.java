package com.quadra.social.application.port.in.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record VisitorDTO(
    Long visitorId,
    String nickname,
    String avatar,
    String source,
    BigDecimal score,
    LocalDateTime visitTime
) {
}
