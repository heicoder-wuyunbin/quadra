package com.quadra.social.application.port.in.query;

import com.quadra.social.application.port.in.dto.FollowerDTO;
import com.quadra.social.application.port.in.dto.PageResult;

public interface ListFollowingQuery {
    /**
     * 查询某用户关注的人列表
     * @param userId 用户ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 关注列表
     */
    PageResult<FollowerDTO> listFollowing(Long userId, int pageNo, int pageSize);
}
