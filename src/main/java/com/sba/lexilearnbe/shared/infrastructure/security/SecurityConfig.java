package com.sba.lexilearnbe.shared.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // bật @PreAuthorize("hasRole('ADMIN')") trên controller/service
@RequiredArgsConstructor
public class SecurityConfig {

    // Các endpoint public, không cần đăng nhập
    // (JwtAuthenticationFilter cũng dùng danh sách này để bỏ qua việc parse token)
    // Liệt kê tường minh từng endpoint auth thay vì wildcard /api/v1/auth/**:
    // tránh việc thêm endpoint cần đăng nhập sau này (vd /me, /change-password) bị
    // vô tình public. Lưu ý /refresh và /logout vẫn phải public — chúng tự xử lý
    // token thủ công và không được để JwtAuthenticationFilter chặn access token hết hạn.
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/register",
            "/api/v1/auth/verify-otp",
            "/api/v1/auth/resend-otp",
            "/api/v1/auth/forgot-password",
            "/api/v1/auth/reset-password",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh",
            "/api/v1/auth/logout",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/api/v1/works/{slug}",
            "/api/v1/works",
            "/api/v1/tags",
            "/api/v1/authors/{slug}",
            "/api/v1/authors",
            "/api/v1/works/{workId}/sections/{sectionId}",
            "/api/v1/works/{workId}/sections",
            "/api/v1/works/{workId}/characters",
            "/api/v1/works/{workId}/artistic-features",
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    // Các origin của frontend được phép gọi API (cách nhau bởi dấu phẩy).
    // Mặc định cho môi trường dev (Vite chạy ở cổng 5173).
    @Value("${app.cors.allowed-origins:http://localhost:5173}")
    private List<String> allowedOrigins;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // dùng JWT, không cần CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()

                        .anyRequest().authenticated()
                )
                // Trả lỗi 401/403 dạng JSON đúng format ApiResponse thay vì response rỗng mặc định
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                // Xác thực JWT trước khi vào các filter authentication mặc định
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Cấu hình CORS để FE (khác origin) gọi được API.
    // Khi bật .cors(), Spring Security cho preflight OPTIONS đi qua (không bị chặn 403).
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // cho phép gửi cookie/Authorization header
        config.setMaxAge(3600L); // cache preflight 1 giờ

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}