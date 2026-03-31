package com.quadra.user.application.port.out;

public interface AdminUserCommandPort {
    int updateStatus(Long userId, Integer status);
    int updatePassword(Long userId, String passwordHash);
}
