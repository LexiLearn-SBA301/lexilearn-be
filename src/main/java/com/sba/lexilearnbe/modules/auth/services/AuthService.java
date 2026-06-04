package com.sba.lexilearnbe.modules.auth.services;

import com.sba.lexilearnbe.modules.auth.dto.request.LoginRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.RefreshTokenRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.RegisterRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.ResendOtpRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.VerifyOtpRequest;
import com.sba.lexilearnbe.modules.auth.dto.response.TokenResponse;

public interface AuthService {

    String OTP_TYPE_REGISTER = "register";

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
}
