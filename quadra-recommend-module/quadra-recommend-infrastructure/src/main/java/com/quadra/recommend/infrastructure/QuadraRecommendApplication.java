package com.quadra.recommend.infrastructure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Quadra 推荐服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.quadra.recommend")
@MapperScan("com.quadra.recommend.adapter.out.persistence.mapper")
@EnableDiscoveryClient
@EnableScheduling
public class QuadraRecommendApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuadraRecommendApplication.class, args);
    }
}
