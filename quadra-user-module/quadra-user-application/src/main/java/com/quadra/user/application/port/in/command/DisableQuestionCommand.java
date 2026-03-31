package com.quadra.user.application.port.in.command;

public record DisableQuestionCommand(Long userId, Long questionId) {}
