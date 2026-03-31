package com.quadra.recommend.application.port.in.command;

import java.math.BigDecimal;

/**
 * 记录用户行为命令
 */
public record RecordActionCommand(
    Long userId,
    String targetType,
    Long targetId,
    String actionType,
    BigDecimal weight
) {
    public RecordActionCommand(Long userId, String targetType, Long targetId, String actionType) {
        this(userId, targetType, targetId, actionType, null);
    }
}
