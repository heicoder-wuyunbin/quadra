package com.quadra.user.application.port.in.command;

import java.time.LocalDate;
import java.util.Map;

public record UpdateProfileCommand(
    Long userId,
    String nickname,
    String avatar,
    Integer gender,
    LocalDate birthday,
    String city,
    String income,
    String profession,
    Integer marriage,
    String coverPic,
    Map<String, Object> tags
) {}
