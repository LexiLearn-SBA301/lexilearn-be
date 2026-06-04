package com.sba.lexilearnbe.modules.auth.controller;

import com.sba.lexilearnbe.modules.auth.dto.request.RegisterRequest;
import com.sba.lexilearnbe.modules.auth.services.AuthService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "API xác thực: đăng ký, đăng nhập, OTP, token")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Đăng ký tài khoản", description = "Tạo account UNVERIFIED và gửi OTP xác thực qua email")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .message("Đăng ký thành công. Vui lòng kiểm tra email để lấy mã OTP xác thực.")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
