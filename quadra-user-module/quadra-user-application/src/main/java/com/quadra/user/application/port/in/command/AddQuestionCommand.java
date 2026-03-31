package com.quadra.user.application.port.in.command;

public record AddQuestionCommand(Long userId, String question, Integer sortOrder) {}
