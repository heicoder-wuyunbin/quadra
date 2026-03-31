package com.quadra.user.application.port.in.command;

public record LoginCommand(String mobile, String rawPassword) {
}
