package com.quadra.user.adapter.out.query;

import com.quadra.user.adapter.out.persistence.mapper.UserMapper;
import com.quadra.user.application.port.in.dto.AdminUserDTO;
import com.quadra.user.application.port.in.dto.AdminUserDetailDTO;
import com.quadra.user.application.port.in.dto.PageResult;
import com.quadra.user.application.port.out.AdminUserQueryPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
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
        log.info("查询用户详情，id={}", id);
        AdminUserDetailDTO detail = userMapper.getAdminUserDetail(id);
        if (detail == null) {
            log.warn("数据库中未找到用户详情，id={}", id);
        } else {
            log.info("查询用户详情成功，id={}, mobile={}", id, detail.mobile());
        }
        return detail;
    }
}
