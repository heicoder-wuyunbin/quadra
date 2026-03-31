package com.quadra.content.infrastructure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.quadra.content")
@MapperScan("com.quadra.content.adapter.out.persistence.mapper")
@EnableDiscoveryClient
@EnableScheduling
public class QuadraContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuadraContentApplication.class, args);
    }
}
