package com.sba.lexilearnbe.modules.auth.services;

import com.sba.lexilearnbe.modules.auth.dto.request.ForgotPasswordRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.LoginRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.RefreshTokenRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.RegisterRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.ResendOtpRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.ResetPasswordRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.VerifyOtpRequest;
import com.sba.lexilearnbe.modules.auth.dto.response.TokenResponse;
import com.sba.lexilearnbe.modules.auth.dto.response.UserResponse;

import java.util.UUID;

public interface AuthService {

    String OTP_TYPE_REGISTER = "register";
    String OTP_TYPE_RESET_PASSWORD = "reset_password";

    /**
     * Đăng ký account mới: tạo account UNVERIFIED + gán role USER,
     * sinh OTP và gửi mail xác thực.
     * Email đã tồn tại nhưng chưa verify → cho đăng ký lại (cập nhật password + OTP mới).
     */
    void register(RegisterRequest request);

    /**
     * Xác thực email bằng OTP: verify mã và kích hoạt account (UNVERIFIED → ACTIVE).
     */
    void verifyRegisterOtp(VerifyOtpRequest request);

    /**
     * Gửi lại OTP xác thực email (OTP cũ hết hạn / thất lạc).
     */
    void resendRegisterOtp(ResendOtpRequest request);

    /**
     * Đăng nhập bằng email + password: account phải ACTIVE,
     * trả về cặp access token (JWT) + refresh token (lưu Redis).
     */
    TokenResponse login(LoginRequest request);

    /**
     * Cấp lại cặp token mới từ refresh token (rotation + reuse detection:
     * token cũ chỉ dùng được 1 lần, token đã dùng mà bị dùng lại
     * → coi như bị trộm, revoke toàn bộ phiên đăng nhập liên quan).
     */
    TokenResponse refresh(RefreshTokenRequest request);

    /**
     * Đăng xuất: thu hồi refresh token + toàn bộ family khỏi Redis,
     * đồng thời blacklist access token (lấy từ header Authorization)
     * đến khi nó hết hạn — token bị đánh cắp cũng không dùng được sau logout.
     *
     * @param authorizationHeader header Authorization (Bearer ...), có thể null
     */
    void logout(RefreshTokenRequest request, String authorizationHeader);

    /**
     * Quên mật khẩu: sinh OTP đặt lại mật khẩu và gửi qua email.
     * Chống dò email (user enumeration): luôn trả về như nhau dù email
     * có tồn tại hay không — chỉ thực sự gửi OTP khi account tồn tại & ACTIVE.
     */
    void forgotPassword(ForgotPasswordRequest request);

    /**
     * Đặt lại mật khẩu bằng OTP: verify mã (type reset_password),
     * cập nhật mật khẩu mới (BCrypt) cho account ACTIVE.
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * Lấy thông tin profile của account đang đăng nhập.
     * Fetch kèm roles (EntityGraph) để tránh LazyInitializationException.
     *
     * @param accountId UUID của account lấy từ JWT principal
     * @return UserResponse chứa id, email, status, roles, emailVerifiedAt, createdAt
     */
    UserResponse getCurrentUser(UUID accountId);
}
