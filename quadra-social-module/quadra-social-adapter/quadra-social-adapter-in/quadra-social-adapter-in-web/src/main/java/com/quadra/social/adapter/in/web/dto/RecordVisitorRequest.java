package com.quadra.social.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "记录访客请求参数")
public record RecordVisitorRequest(
    @Schema(description = "被访问主页的用户ID", example = "1234567890")
    Long targetUserId,

    @Schema(description = "访问来源", example = "PROFILE")
    String source,

    @Schema(description = "访客缘分得分", example = "0.88")
    BigDecimal score
) {}
