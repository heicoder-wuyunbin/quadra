package com.quadra.system.application.port.in.command;

public record AdminLoginCommand(
    String username,
    String password
) {}
