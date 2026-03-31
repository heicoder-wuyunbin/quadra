package com.quadra.system.adapter.out.scheduler;

import com.quadra.system.application.port.in.GenerateDailyAnalysisUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 每日数据统计调度器
 * 每日凌晨统计前一日数据
 */
@Component
public class DailyAnalysisScheduler {

    private static final Logger log = LoggerFactory.getLogger(DailyAnalysisScheduler.class);

    private final GenerateDailyAnalysisUseCase generateDailyAnalysisUseCase;

    public DailyAnalysisScheduler(GenerateDailyAnalysisUseCase generateDailyAnalysisUseCase) {
        this.generateDailyAnalysisUseCase = generateDailyAnalysisUseCase;
    }

    /**
     * 每日凌晨 1:00 执行
     * 统计前一日数据
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void generateDailyStatistics() {
        log.info("Starting daily analysis generation...");
        
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            generateDailyAnalysisUseCase.generateDailyAnalysis(yesterday);
            log.info("Daily analysis for {} completed successfully", yesterday);
        } catch (Exception e) {
            log.error("Failed to generate daily analysis", e);
        }
    }
}
