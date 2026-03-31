package com.quadra.recommend.application.port.in.query;

import com.quadra.recommend.application.port.in.dto.PageResult;
import com.quadra.recommend.application.port.in.dto.RecommendUserDTO;

/**
 * 获取推荐用户查询接口
 */
public interface GetRecommendUsersQuery {
    
    /**
     * 获取今日推荐用户列表
     * @param userId 当前用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页推荐用户结果
     */
    PageResult<RecommendUserDTO> getTodayRecommendUsers(Long userId, int pageNum, int pageSize);
}
