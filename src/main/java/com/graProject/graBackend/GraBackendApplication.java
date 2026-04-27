package com.graProject.graBackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring Boot 启动类。
 */
@SpringBootApplication
@EnableScheduling
@MapperScan("com.graProject.graBackend.mapper")
public class GraBackendApplication {

    /**
     * 应用启动入口。
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(GraBackendApplication.class, args);
    }
}
