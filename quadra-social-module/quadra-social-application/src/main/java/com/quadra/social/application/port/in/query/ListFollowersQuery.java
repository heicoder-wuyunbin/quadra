package com.quadra.social.application.port.in.query;

import com.quadra.social.application.port.in.dto.FollowerDTO;
import com.quadra.social.application.port.in.dto.PageResult;

public interface ListFollowersQuery {
    /**
     * 查询某用户的粉丝列表
     * @param userId 用户ID
     * @param pageNo 页码
     * @param pageSize 每页大小
     * @return 粉丝列表
     */
    PageResult<FollowerDTO> listFollowers(Long userId, int pageNo, int pageSize);
}
