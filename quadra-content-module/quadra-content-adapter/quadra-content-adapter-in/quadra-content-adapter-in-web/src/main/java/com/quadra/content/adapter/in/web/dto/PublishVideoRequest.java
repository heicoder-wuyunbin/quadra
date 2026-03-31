package com.quadra.content.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "发布短视频请求")
public record PublishVideoRequest(
    @Schema(description = "视频URL")
    String videoUrl,
    
    @Schema(description = "封面URL")
    String coverUrl,
    
    @Schema(description = "视频时长(秒)")
    Integer duration,
    
    @Schema(description = "视频描述")
    String description
) {}
