package com.sba.lexilearnbe.modules.auth.controller;

import com.sba.lexilearnbe.modules.auth.dto.request.LoginRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.RefreshTokenRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.RegisterRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.ResendOtpRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.VerifyOtpRequest;
import com.sba.lexilearnbe.modules.auth.dto.response.TokenResponse;
import com.sba.lexilearnbe.modules.auth.services.AuthService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @PostMapping("/verify-otp")
    @Operation(summary = "Xác thực email bằng OTP", description = "Verify mã OTP và kích hoạt account (UNVERIFIED → ACTIVE)")
    public ResponseEntity<ApiResponse<Void>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        authService.verifyRegisterOtp(request);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .message("Xác thực email thành công. Bạn có thể đăng nhập ngay bây giờ.")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-otp")
    @Operation(summary = "Gửi lại OTP xác thực email", description = "Sinh OTP mới và gửi lại qua email (tối đa 5 lần/giờ)")
    public ResponseEntity<ApiResponse<Void>> resendOtp(@Valid @RequestBody ResendOtpRequest request) {
        authService.resendRegisterOtp(request);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .message("Đã gửi lại mã OTP. Vui lòng kiểm tra email.")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Đăng nhập", description = "Xác thực email + password, trả về access token (JWT) và refresh token")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse tokens = authService.login(request);

        ApiResponse<TokenResponse> response = ApiResponse.<TokenResponse>builder()
                .message("Đăng nhập thành công.")
                .result(tokens)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Làm mới token", description = "Đổi refresh token lấy cặp token mới (refresh token cũ bị thu hồi ngay)")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse tokens = authService.refresh(request);

        ApiResponse<TokenResponse> response = ApiResponse.<TokenResponse>builder()
                .message("Làm mới token thành công.")
                .result(tokens)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Đăng xuất", description = "Thu hồi refresh token và blacklist access token (gửi kèm header Authorization) đến khi hết hạn")
    public ResponseEntity<ApiResponse<Void>> logout(
            @Valid @RequestBody RefreshTokenRequest request,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        authService.logout(request, authorizationHeader);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .message("Đăng xuất thành công.")
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.ok(response);
    }
}
