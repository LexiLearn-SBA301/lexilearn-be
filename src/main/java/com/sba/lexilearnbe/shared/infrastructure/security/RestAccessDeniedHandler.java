package com.sba.lexilearnbe.shared.infrastructure.security;

import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Chặn request đã đăng nhập nhưng không đủ quyền (403) ở tầng filter chain.
 * GlobalExceptionHandler chỉ bắt được AccessDeniedException từ method security (@PreAuthorize),
 * còn lỗi từ authorizeHttpRequests phải xử lý ở đây.
 */
@Component
@RequiredArgsConstructor
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final SecurityErrorResponseWriter errorWriter;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        errorWriter.write(request, response, ErrorCode.FORBIDDEN);
    }
}
