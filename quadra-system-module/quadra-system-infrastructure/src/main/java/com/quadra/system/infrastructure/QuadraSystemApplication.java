package com.quadra.system.infrastructure;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.quadra.system")
@MapperScan("com.quadra.system.adapter.out.persistence.mapper")
@EnableDiscoveryClient
@EnableScheduling
@EnableFeignClients(basePackages = "com.quadra.system.adapter.out.client")
public class QuadraSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(QuadraSystemApplication.class, args);
    }
}
