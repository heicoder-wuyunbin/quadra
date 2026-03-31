package com.quadra.system.application.port.out;

public interface UserCommandPort {
    int updateStatus(Long userId, Integer status);
    String resetPassword(Long userId);
}
