package com.quadra.user.application.port.out;

public interface PasswordEncoderPort {
    /**
     * 密码加密（如 BCrypt）
     */
    String encode(String rawPassword);
}
