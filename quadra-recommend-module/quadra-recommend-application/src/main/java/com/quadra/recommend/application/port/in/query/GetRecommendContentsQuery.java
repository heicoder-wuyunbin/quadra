package com.quadra.recommend.application.port.in.query;

import com.quadra.recommend.application.port.in.dto.PageResult;
import com.quadra.recommend.application.port.in.dto.RecommendContentDTO;

/**
 * 获取推荐内容查询接口
 */
public interface GetRecommendContentsQuery {
    
    /**
     * 获取推荐内容列表
     * @param userId 当前用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页推荐内容结果
     */
    PageResult<RecommendContentDTO> getRecommendContents(Long userId, int pageNum, int pageSize);
}
