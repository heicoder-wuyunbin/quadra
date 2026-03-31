package com.quadra.recommend.application.port.out;

import com.quadra.recommend.application.port.in.dto.PageResult;
import com.quadra.recommend.application.port.in.dto.RecommendUserDTO;
import com.quadra.recommend.application.port.in.dto.RecommendContentDTO;

/**
 * 推荐查询端口
 */
public interface RecommendQueryPort {
    
    /**
     * 查询今日推荐用户列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页推荐用户结果
     */
    PageResult<RecommendUserDTO> findTodayRecommendUsers(Long userId, int pageNum, int pageSize);
    
    /**
     * 查询推荐内容列表
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页推荐内容结果
     */
    PageResult<RecommendContentDTO> findRecommendContents(Long userId, int pageNum, int pageSize);
}
