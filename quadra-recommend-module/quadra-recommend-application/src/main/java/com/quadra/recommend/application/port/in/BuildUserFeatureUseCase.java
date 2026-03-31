package com.quadra.recommend.application.port.in;

/**
 * 构建用户特征用例接口
 */
public interface BuildUserFeatureUseCase {
    
    /**
     * 为单个用户构建特征
     * @param userId 用户ID
     */
    void buildUserFeature(Long userId);
    
    /**
     * 为所有用户批量构建特征
     */
    void buildAllUserFeatures();
}
