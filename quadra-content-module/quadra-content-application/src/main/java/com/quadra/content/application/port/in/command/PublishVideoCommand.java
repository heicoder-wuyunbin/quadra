package com.quadra.content.application.port.in.command;

/**
 * 发布短视频指令
 */
public record PublishVideoCommand(
    Long userId,
    String videoUrl,
    String coverUrl,
    Integer duration,
    String description
) {}
