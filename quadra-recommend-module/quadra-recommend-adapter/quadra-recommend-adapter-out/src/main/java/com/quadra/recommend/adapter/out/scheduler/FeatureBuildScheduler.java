package com.quadra.recommend.adapter.out.scheduler;

import com.quadra.recommend.application.port.in.BuildUserFeatureUseCase;
import com.quadra.recommend.application.port.in.BuildRecommendUserUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 特征构建调度器
 * 定时构建用户特征和推荐结果
 */
@Component
public class FeatureBuildScheduler {

    private static final Logger log = LoggerFactory.getLogger(FeatureBuildScheduler.class);
    
    private final BuildUserFeatureUseCase buildUserFeatureUseCase;
    private final BuildRecommendUserUseCase buildRecommendUserUseCase;

    public FeatureBuildScheduler(
            BuildUserFeatureUseCase buildUserFeatureUseCase,
            BuildRecommendUserUseCase buildRecommendUserUseCase) {
        this.buildUserFeatureUseCase = buildUserFeatureUseCase;
        this.buildRecommendUserUseCase = buildRecommendUserUseCase;
    }

    /**
     * 每天凌晨 2 点构建所有用户特征
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void buildUserFeatures() {
        log.info("Starting scheduled user feature build task");
        try {
            buildUserFeatureUseCase.buildAllUserFeatures();
            log.info("Completed scheduled user feature build task");
        } catch (Exception e) {
            log.error("Failed to complete user feature build task", e);
        }
    }

    /**
     * 每天凌晨 3 点构建所有推荐结果
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void buildRecommendUsers() {
        log.info("Starting scheduled recommend user build task");
        try {
            buildRecommendUserUseCase.buildAllRecommendUsers();
            log.info("Completed scheduled recommend user build task");
        } catch (Exception e) {
            log.error("Failed to complete recommend user build task", e);
        }
    }

    /**
     * 每小时增量更新用户特征（可选）
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void incrementalBuildUserFeatures() {
        log.info("Starting incremental user feature build task");
        // 实现增量更新逻辑
        log.info("Completed incremental user feature build task");
    }
}
