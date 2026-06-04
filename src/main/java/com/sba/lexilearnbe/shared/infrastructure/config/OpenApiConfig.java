package com.sba.lexilearnbe.shared.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình Swagger UI: nút Authorize (Bearer JWT) áp dụng global cho mọi endpoint
 * — khớp với SecurityConfig (mặc định anyRequest().authenticated()).
 * Endpoint public (vd AuthController) tự gỡ ổ khóa bằng @SecurityRequirements rỗng.
 */
@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_BEARER = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LexiLearn API")
                        .description("REST API cho hệ thống LexiLearn")
                        .version("v1"))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_BEARER,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Dán access token lấy từ /auth/login (không cần prefix 'Bearer ')")))
                // Áp security requirement global: mọi endpoint hiện ổ khóa + tự gắn header Authorization
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_BEARER));
    }
}
