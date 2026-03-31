package com.quadra.user.adapter.out.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.quadra.user.adapter.out.persistence.entity.UserBlacklistDO;
import com.quadra.user.adapter.out.persistence.mapper.UserBlacklistMapper;
import com.quadra.user.application.port.out.BlacklistRepositoryPort;
import com.quadra.user.domain.model.blacklist.UserBlacklist;
import org.springframework.stereotype.Repository;

@Repository
public class BlacklistRepositoryImpl implements BlacklistRepositoryPort {

    private final UserBlacklistMapper blacklistMapper;

    public BlacklistRepositoryImpl(UserBlacklistMapper blacklistMapper) {
        this.blacklistMapper = blacklistMapper;
    }

    @Override
    public void save(UserBlacklist blacklist) {
        UserBlacklistDO DO = new UserBlacklistDO();
        DO.setId(blacklist.getId());
        DO.setUserId(blacklist.getUserId());
        DO.setTargetUserId(blacklist.getTargetUserId());
        DO.setCreateTime(blacklist.getCreateTime());
        blacklistMapper.insert(DO);
    }

    @Override
    public void remove(Long userId, Long targetUserId) {
        LambdaQueryWrapper<UserBlacklistDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBlacklistDO::getUserId, userId)
               .eq(UserBlacklistDO::getTargetUserId, targetUserId);
        blacklistMapper.delete(wrapper);
    }

    @Override
    public boolean exists(Long userId, Long targetUserId) {
        LambdaQueryWrapper<UserBlacklistDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserBlacklistDO::getUserId, userId)
               .eq(UserBlacklistDO::getTargetUserId, targetUserId);
        return blacklistMapper.exists(wrapper);
    }

    @Override
    public Long nextId() {
        return IdWorker.getId();
    }
}
