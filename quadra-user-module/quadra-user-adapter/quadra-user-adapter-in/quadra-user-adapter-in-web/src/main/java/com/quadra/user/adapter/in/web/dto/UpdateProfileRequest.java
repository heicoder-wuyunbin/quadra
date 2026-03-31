package com.quadra.user.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.Map;

@Schema(description = "更新用户资料请求参数")
public record UpdateProfileRequest(
    @Schema(description = "昵称", example = "张三")
    String nickname,
    
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    String avatar,
    
    @Schema(description = "性别: 0-未知, 1-男, 2-女", example = "1")
    Integer gender,
    
    @Schema(description = "生日", example = "1990-01-01")
    LocalDate birthday,
    
    @Schema(description = "城市", example = "北京市")
    String city,
    
    @Schema(description = "收入", example = "20k-30k")
    String income,
    
    @Schema(description = "职业", example = "软件工程师")
    String profession,
    
    @Schema(description = "婚姻状况: 0-未婚, 1-离异, 2-丧偶", example = "0")
    Integer marriage,
    
    @Schema(description = "封面图", example = "https://example.com/cover.jpg")
    String coverPic,
    
    @Schema(description = "动态标签(JSON格式)")
    Map<String, Object> tags
) {}
