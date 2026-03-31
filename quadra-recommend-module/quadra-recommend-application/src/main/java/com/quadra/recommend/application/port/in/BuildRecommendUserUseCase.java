package com.quadra.recommend.application.port.in;

/**
 * 构建推荐用户结果用例接口
 */
public interface BuildRecommendUserUseCase {
    
    /**
     * 为单个用户构建今日推荐
     * @param userId 用户ID
     */
    void buildRecommendUser(Long userId);
    
    /**
     * 为所有用户批量构建今日推荐
     */
    void buildAllRecommendUsers();
}
