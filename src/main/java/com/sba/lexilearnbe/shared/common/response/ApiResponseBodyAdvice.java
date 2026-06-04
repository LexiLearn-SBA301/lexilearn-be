package com.sba.lexilearnbe.shared.common.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;

/**
 * Vá các field chung (path, timestamp) cho mọi response có body là {@link ApiResponse}
 * trước khi serialize — controller/handler chỉ cần set code/message/result,
 * không phải lặp lại boilerplate ở từng endpoint.
 *
 * Lưu ý: chỉ cover response đi qua Spring MVC. Lỗi ghi thẳng ở tầng filter
 * (SecurityErrorResponseWriter) không qua đây — bên đó tự set đủ field.
 */
@RestControllerAdvice
public class ApiResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Lọc chính xác theo kiểu body trong beforeBodyWrite (generic bị xóa lúc runtime,
        // check ở đây theo return type của method không bắt được ResponseEntity<ApiResponse<...>>)
        return true;
    }

    @Override
    public Object beforeBodyWrite(@Nullable Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof ApiResponse<?> apiResponse) {
            if (apiResponse.getPath() == null) {
                apiResponse.setPath(request.getURI().getPath());
            }
            if (apiResponse.getTimestamp() == null) {
                apiResponse.setTimestamp(LocalDateTime.now());
            }
        }
        return body;
    }
}
