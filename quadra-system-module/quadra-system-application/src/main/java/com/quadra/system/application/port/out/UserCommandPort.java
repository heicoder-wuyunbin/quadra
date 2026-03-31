package com.quadra.system.application.port.out;

public interface UserCommandPort {
    int updateStatus(String userId, Integer status);
    String resetPassword(String userId);
}
