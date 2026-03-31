package com.quadra.user.infrastructure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.quadra.user")
@MapperScan("com.quadra.user.adapter.out.persistence.mapper")
@EnableDiscoveryClient
@EnableScheduling
public class QuadraUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuadraUserApplication.class, args);
    }
}