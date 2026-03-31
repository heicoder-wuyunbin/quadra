package com.quadra.social.application.port.in.command;

import java.math.BigDecimal;

public record RecordVisitorCommand(Long userId, Long visitorId, String source, BigDecimal score) {
}
