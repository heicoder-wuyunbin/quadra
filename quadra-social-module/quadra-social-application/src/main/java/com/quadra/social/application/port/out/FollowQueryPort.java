package com.quadra.social.application.port.out;

import com.quadra.social.application.port.in.dto.FollowerDTO;
import com.quadra.social.application.port.in.dto.PageResult;

public interface FollowQueryPort {
    /**
     * 查询粉丝列表（分页）
     */
    PageResult<FollowerDTO> findFollowers(Long userId, int pageNo, int pageSize);
    
    /**
     * 查询关注列表（分页）
     */
    PageResult<FollowerDTO> findFollowing(Long userId, int pageNo, int pageSize);
}
