package com.quadra.recommend.application.service;

import com.quadra.recommend.application.port.in.query.GetRecommendUsersQuery;
import com.quadra.recommend.application.port.in.query.GetRecommendContentsQuery;
import com.quadra.recommend.application.port.in.dto.PageResult;
import com.quadra.recommend.application.port.in.dto.RecommendUserDTO;
import com.quadra.recommend.application.port.in.dto.RecommendContentDTO;
import com.quadra.recommend.application.port.out.RecommendQueryPort;
import org.springframework.stereotype.Service;

/**
 * 推荐查询服务
 * 实现获取推荐用户和推荐内容查询
 */
@Service
public class RecommendQueryService implements GetRecommendUsersQuery, GetRecommendContentsQuery {

    private final RecommendQueryPort recommendQueryPort;

    public RecommendQueryService(RecommendQueryPort recommendQueryPort) {
        this.recommendQueryPort = recommendQueryPort;
    }

    @Override
    public PageResult<RecommendUserDTO> getTodayRecommendUsers(Long userId, int pageNum, int pageSize) {
        return recommendQueryPort.findTodayRecommendUsers(userId, pageNum, pageSize);
    }

    @Override
    public PageResult<RecommendContentDTO> getRecommendContents(Long userId, int pageNum, int pageSize) {
        return recommendQueryPort.findRecommendContents(userId, pageNum, pageSize);
    }
}
