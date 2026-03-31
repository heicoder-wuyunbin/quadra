package com.quadra.social.infrastructure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.quadra.social")
@MapperScan("com.quadra.social.adapter.out.persistence.mapper")
@EnableDiscoveryClient
@EnableScheduling
public class QuadraSocialApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuadraSocialApplication.class, args);
    }
}
