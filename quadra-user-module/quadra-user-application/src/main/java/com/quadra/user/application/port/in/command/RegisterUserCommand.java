package com.quadra.user.application.port.in.command;

public record RegisterUserCommand(String mobile, String rawPassword) {
}
