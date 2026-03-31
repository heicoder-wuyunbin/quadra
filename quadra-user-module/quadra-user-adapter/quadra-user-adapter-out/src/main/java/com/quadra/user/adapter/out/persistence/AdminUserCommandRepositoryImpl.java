package com.quadra.user.adapter.out.persistence;

import com.quadra.user.adapter.out.persistence.mapper.UserMapper;
import com.quadra.user.application.port.out.AdminUserCommandPort;
import org.springframework.stereotype.Repository;

@Repository
public class AdminUserCommandRepositoryImpl implements AdminUserCommandPort {

    private final UserMapper userMapper;

    public AdminUserCommandRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public int updateStatus(Long userId, Integer status) {
        return userMapper.updateUserStatus(userId, status);
    }

    @Override
    public int updatePassword(Long userId, String passwordHash) {
        return userMapper.updateUserPassword(userId, passwordHash);
    }
}
