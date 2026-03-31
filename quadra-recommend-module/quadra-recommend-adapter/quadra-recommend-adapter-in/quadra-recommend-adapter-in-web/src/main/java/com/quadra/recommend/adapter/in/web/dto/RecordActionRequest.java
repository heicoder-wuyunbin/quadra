package com.quadra.recommend.adapter.in.web.dto;

import java.math.BigDecimal;

/**
 * 记录用户行为请求
 */
public record RecordActionRequest(
    String targetType,
    Long targetId,
    String actionType,
    BigDecimal weight
) {}
