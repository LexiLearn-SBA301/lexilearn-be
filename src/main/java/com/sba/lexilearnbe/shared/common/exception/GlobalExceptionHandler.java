package com.sba.lexilearnbe.shared.common.exception;

import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

//    Đây là nơi bắt lỗi mà mình đã define trong ApiException,
//    khi mình throw ApiException ở bất kỳ đâu trong code thì sẽ được bắt ở đây và trả về response theo đúng format mà mình đã định nghĩa trong ApiResponse
    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex, HttpServletRequest request) {

        String responseMessage = (ex.getCustomMessage() != null) ? ex.getCustomMessage() : ex.getMessage();

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(ex.getErrorCode().getCode())
                .message(responseMessage)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ex.getErrorCode().getStatus()).body(response);
    }

//    2. Bắt lỗi validation ở DTO
    @ExceptionHandler(value = MethodArgumentNotValidException.class) // Đây là lỗi khi validate dữ liệu đầu vào của DTO thất bại, ví dụ như @NotNull, @Size, ...
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        // Gom lỗi theo từng field: { "email": "Email không hợp lệ", "password": "..." }
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .code(ErrorCode.VALIDATION_ERROR.getCode())
                .message(ErrorCode.VALIDATION_ERROR.getMessage())
                .result(fieldErrors)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getStatus()).body(response);
    }

//    3. Bắt các lỗi trên URL
    @ExceptionHandler(value = ConstraintViolationException.class) // Đây là lỗi khi có lỗi validate trên URL, ví dụ như @RequestParam, @PathVariable, ...
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request) {
        // Gom lỗi theo từng param trên URL: { "register.email": "Email không hợp lệ" }
        Map<String, String> violations = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(violation ->
                violations.putIfAbsent(violation.getPropertyPath().toString(), violation.getMessage()));

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .code(ErrorCode.VALIDATION_ERROR.getCode())
                .message(ErrorCode.VALIDATION_ERROR.getMessage())
                .result(violations)
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getStatus()).body(response);
    }

//    4. Lỗi truyền sai định dạng JSON
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(ErrorCode.INVALID_FORMAT.getCode())
                .message(ErrorCode.INVALID_FORMAT.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.INVALID_FORMAT.getStatus()).body(response);
    }


//    5. Lỗi 404 Not Found
    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(ErrorCode.API_NOT_FOUND.getCode())
                .message(ErrorCode.API_NOT_FOUND.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.API_NOT_FOUND.getStatus()).body(response);
    }

//    6. Bắt lỗi 403 access denied
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(ErrorCode.FORBIDDEN.getCode())
                .message(ErrorCode.FORBIDDEN.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.FORBIDDEN.getStatus()).body(response);
    }

//    7. Bắt lỗi 401 unauthenticated (chưa đăng nhập), lỗi này sẽ được Spring Security tự động bắt và trả về response 401 nhưng vì trả về đúng theo response của mình nên phải custom lại
@ExceptionHandler(value = AuthenticationCredentialsNotFoundException.class)
public ResponseEntity<ApiResponse<Void>> handleAuthenticationCredentialsNotFoundException(AuthenticationCredentialsNotFoundException ex, HttpServletRequest request) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(ErrorCode.UNAUTHENTICATED.getCode())
                .message(ErrorCode.UNAUTHENTICATED.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.UNAUTHENTICATED.getStatus()).body(response);
}


//    Bắt các lỗi nội bộ hệ thống
    @ExceptionHandler(value = Exception.class) // Bắt tất cả các lỗi khác mà mình chưa định nghĩa trong ApiException
    public ResponseEntity<ApiResponse<Void>> handleUncategorizedException(Exception ex, HttpServletRequest request) {
        log.error("Lỗi hệ thống không xác định: ", ex); // Log lỗi để dễ dàng debug

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(ErrorCode.INTERNAL_SERVER_ERROR.getCode())
                .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(response);
    }



}
