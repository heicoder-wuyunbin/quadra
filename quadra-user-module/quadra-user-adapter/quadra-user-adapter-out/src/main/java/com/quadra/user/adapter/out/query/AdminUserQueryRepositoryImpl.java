package com.quadra.user.adapter.out.query;

import com.quadra.user.adapter.out.persistence.mapper.UserMapper;
import com.quadra.user.application.port.in.dto.AdminUserDTO;
import com.quadra.user.application.port.in.dto.AdminUserDetailDTO;
import com.quadra.user.application.port.in.dto.PageResult;
import com.quadra.user.application.port.out.AdminUserQueryPort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AdminUserQueryRepositoryImpl implements AdminUserQueryPort {

    private final UserMapper userMapper;

    public AdminUserQueryRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public PageResult<AdminUserDTO> findUsers(String mobile, Integer status, int page, int size) {
        int pageNo = Math.max(page, 1);
        int pageSize = Math.max(size, 1);
        long offset = (long) (pageNo - 1) * pageSize;

        long total = userMapper.countAdminUsers(mobile, status);
        List<AdminUserDTO> records = userMapper.listAdminUsers(mobile, status, offset, pageSize);
        return PageResult.of(records, total, pageNo, pageSize);
    }

    @Override
    public AdminUserDetailDTO findUserDetail(Long id) {
        return userMapper.getAdminUserDetail(id);
    }
}
