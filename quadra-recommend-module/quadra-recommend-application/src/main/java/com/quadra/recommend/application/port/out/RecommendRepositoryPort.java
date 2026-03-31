package com.quadra.recommend.application.port.out;

import com.quadra.recommend.domain.model.RecommendUser;
import com.quadra.recommend.domain.model.RecommendContent;
import java.util.List;

/**
 * 推荐仓库端口
 */
public interface RecommendRepositoryPort {
    
    /**
     * 获取下一个ID
     * @return 雪花算法生成的ID
     */
    Long nextId();
    
    /**
     * 批量保存用户推荐结果
     * @param recommendUsers 推荐用户结果列表
     */
    void saveAllRecommendUsers(List<RecommendUser> recommendUsers);
    
    /**
     * 批量保存内容推荐结果
     * @param recommendContents 推荐内容结果列表
     */
    void saveAllRecommendContents(List<RecommendContent> recommendContents);
    
    /**
     * 删除指定用户今日的推荐结果
     * @param userId 用户ID
     * @param recommendDate 推荐日期
     */
    void deleteTodayRecommendUsers(Long userId, java.time.LocalDate recommendDate);
}
