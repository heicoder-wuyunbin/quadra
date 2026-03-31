package com.quadra.user.application.port.in.command;

public record UpdateQuestionCommand(Long userId, Long questionId, String question, Integer sortOrder) {}
