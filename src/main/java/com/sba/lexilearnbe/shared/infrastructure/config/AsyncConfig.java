package com.sba.lexilearnbe.shared.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Bật @Async cho các tác vụ chạy nền (vd: gửi mail OTP sau khi commit).
 * Dùng applicationTaskExecutor mặc định của Spring Boot
 * (cấu hình qua spring.task.execution.* nếu cần chỉnh pool).
 */
@Configuration
@EnableAsync
public class AsyncConfig {
}
