package com.quadra.interaction.infrastructure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.quadra.interaction")
@MapperScan("com.quadra.interaction.adapter.out.persistence.mapper")
@EnableDiscoveryClient
@EnableScheduling
public class QuadraInteractionApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuadraInteractionApplication.class, args);
    }
}
