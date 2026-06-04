package com.sba.lexilearnbe.modules.auth.services;

import com.sba.lexilearnbe.modules.auth.dto.request.RegisterRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.ResendOtpRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.VerifyOtpRequest;

public interface AuthService {

    String OTP_TYPE_REGISTER = "register";

    /**
     * Đăng ký account mới: tạo account UNVERIFIED + gán role USER,
     * sinh OTP và gửi mail xác thực.
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
}
