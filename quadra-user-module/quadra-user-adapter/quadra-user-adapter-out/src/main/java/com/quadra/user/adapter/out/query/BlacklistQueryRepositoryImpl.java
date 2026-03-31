package com.quadra.user.adapter.out.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quadra.user.adapter.out.persistence.entity.UserBlacklistDO;
import com.quadra.user.adapter.out.persistence.mapper.UserBlacklistMapper;
import com.quadra.user.application.port.in.dto.BlacklistItemDTO;
import com.quadra.user.application.port.in.dto.PageResult;
import com.quadra.user.application.port.out.BlacklistQueryPort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BlacklistQueryRepositoryImpl implements BlacklistQueryPort {

    private final UserBlacklistMapper userBlacklistMapper;

    public BlacklistQueryRepositoryImpl(UserBlacklistMapper userBlacklistMapper) {
        this.userBlacklistMapper = userBlacklistMapper;
    }

    @Override
    public PageResult<BlacklistItemDTO> listByUserId(Long userId, int pageNo, int pageSize) {
        Page<UserBlacklistDO> page = new Page<>(Math.max(pageNo, 1), Math.max(pageSize, 1));
        LambdaQueryWrapper<UserBlacklistDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBlacklistDO::getUserId, userId)
                .orderByDesc(UserBlacklistDO::getCreateTime);
        Page<UserBlacklistDO> result = userBlacklistMapper.selectPage(page, wrapper);
        List<BlacklistItemDTO> records = result.getRecords().stream()
                .map(item -> new BlacklistItemDTO(item.getId(), item.getTargetUserId(), item.getCreateTime()))
                .toList();
        return PageResult.of(records, result.getTotal(), (int) result.getCurrent(), (int) result.getSize());
    }
}
