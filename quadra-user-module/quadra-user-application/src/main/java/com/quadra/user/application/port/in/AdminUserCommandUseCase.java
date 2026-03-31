package com.quadra.user.application.port.in;

public interface AdminUserCommandUseCase {
    void updateStatus(Long userId, Integer status);
    String resetPassword(Long userId);
}
