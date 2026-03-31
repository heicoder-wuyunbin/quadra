package com.quadra.social.adapter.out.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quadra.social.adapter.out.persistence.entity.UserFollowDO;
import com.quadra.social.adapter.out.persistence.mapper.UserFollowMapper;
import com.quadra.social.application.port.in.dto.FollowerDTO;
import com.quadra.social.application.port.in.dto.PageResult;
import com.quadra.social.application.port.out.FollowQueryPort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FollowQueryRepositoryImpl implements FollowQueryPort {

    private final UserFollowMapper userFollowMapper;

    public FollowQueryRepositoryImpl(UserFollowMapper userFollowMapper) {
        this.userFollowMapper = userFollowMapper;
    }

    @Override
    public PageResult<FollowerDTO> findFollowers(Long userId, int pageNo, int pageSize) {
        // 查询粉丝列表：targetUserId = userId
        Page<UserFollowDO> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<UserFollowDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollowDO::getTargetUserId, userId)
               .eq(UserFollowDO::getDeleted, 0)
               .orderByDesc(UserFollowDO::getCreatedAt);
        
        Page<UserFollowDO> result = userFollowMapper.selectPage(page, wrapper);
        
        List<FollowerDTO> followers = result.getRecords().stream()
            .map(this::convertToFollowerDTO)
            .toList();
        
        return PageResult.of(followers, result.getTotal(), pageNo, pageSize);
    }

    @Override
    public PageResult<FollowerDTO> findFollowing(Long userId, int pageNo, int pageSize) {
        // 查询关注列表：userId = userId
        Page<UserFollowDO> page = new Page<>(pageNo, pageSize);
        LambdaQueryWrapper<UserFollowDO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFollowDO::getUserId, userId)
               .eq(UserFollowDO::getDeleted, 0)
               .orderByDesc(UserFollowDO::getCreatedAt);
        
        Page<UserFollowDO> result = userFollowMapper.selectPage(page, wrapper);
        
        List<FollowerDTO> following = result.getRecords().stream()
            .map(this::convertToFollowingDTO)
            .toList();
        
        return PageResult.of(following, result.getTotal(), pageNo, pageSize);
    }

    private FollowerDTO convertToFollowerDTO(UserFollowDO userFollowDO) {
        // 注意：这里需要关联查询用户资料
        // 实际生产环境中应该通过 JOIN 或调用 User 服务获取
        // 这里简化处理，仅返回基本 ID 信息
        return new FollowerDTO(
            userFollowDO.getUserId(),
            null, // nickname 需要关联查询
            null, // avatar 需要关联查询
            userFollowDO.getCreatedAt()
        );
    }

    private FollowerDTO convertToFollowingDTO(UserFollowDO userFollowDO) {
        return new FollowerDTO(
            userFollowDO.getTargetUserId(),
            null, // nickname 需要关联查询
            null, // avatar 需要关联查询
            userFollowDO.getCreatedAt()
        );
    }
}
