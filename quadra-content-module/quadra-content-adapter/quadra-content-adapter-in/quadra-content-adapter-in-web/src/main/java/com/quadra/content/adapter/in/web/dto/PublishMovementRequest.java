package com.quadra.content.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "发布图文动态请求")
public record PublishMovementRequest(
    @Schema(description = "文本内容")
    String textContent,
    
    @Schema(description = "媒体列表")
    List<MediaInfo> medias,
    
    @Schema(description = "经度")
    BigDecimal longitude,
    
    @Schema(description = "纬度")
    BigDecimal latitude,
    
    @Schema(description = "位置名称")
    String locationName,
    
    @Schema(description = "审核状态：0-未审核，1-通过")
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
