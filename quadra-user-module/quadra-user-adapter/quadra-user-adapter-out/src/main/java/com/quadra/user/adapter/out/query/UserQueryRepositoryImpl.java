package com.quadra.user.adapter.out.query;

import com.quadra.user.adapter.out.persistence.mapper.UserMapper;
import com.quadra.user.application.port.in.dto.UserProfileDTO;
import com.quadra.user.application.port.out.UserQueryPort;
import org.springframework.stereotype.Repository;

@Repository
public class UserQueryRepositoryImpl implements UserQueryPort {

    private final UserMapper userMapper;

    public UserQueryRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserProfileDTO findProfileById(Long userId) {
        return userMapper.findProfileDtoById(userId);
    }
}
