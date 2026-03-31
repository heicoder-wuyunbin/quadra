package com.quadra.user.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "更新用户偏好设置请求参数")
public record UpdateSettingRequest(
    @Schema(description = "点赞通知: 0-关闭, 1-开启", example = "1")
    Integer likeNotification,
    
    @Schema(description = "评论通知: 0-关闭, 1-开启", example = "1")
    Integer commentNotification,
    
    @Schema(description = "系统通知: 0-关闭, 1-开启", example = "1")
    Integer systemNotification
) {}
