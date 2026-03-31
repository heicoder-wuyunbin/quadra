package com.quadra.recommend.application.port.out;

import com.quadra.recommend.domain.model.UserFeature;
import com.quadra.recommend.domain.model.ContentFeature;
import java.util.List;

/**
 * 特征仓库端口
 */
public interface FeatureRepositoryPort {
    
    /**
     * 获取下一个ID
     * @return 雪花算法生成的ID
     */
    Long nextId();
    
    /**
     * 保存或更新用户特征
     * @param userFeature 用户特征聚合根
     */
    void upsertUserFeature(UserFeature userFeature);
    
    /**
     * 根据用户ID查找用户特征
     * @param userId 用户ID
     * @return 用户特征聚合根
     */
    UserFeature findUserFeatureByUserId(Long userId);
    
    /**
     * 保存或更新内容特征
     * @param contentFeature 内容特征聚合根
     */
    void upsertContentFeature(ContentFeature contentFeature);
    
    /**
     * 获取所有用户ID列表
     * @return 用户ID列表
     */
    List<Long> findAllUserIds();
}
