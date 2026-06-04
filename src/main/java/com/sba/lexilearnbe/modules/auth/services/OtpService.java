package com.sba.lexilearnbe.modules.auth.services;

public interface OtpService {

    /**
     * Sinh OTP, lưu vào Redis và check rate limit (chống spam gửi mail).
     *
     * @param type loại OTP, ví dụ "register", "reset_password"
     * @return mã OTP vừa sinh
     */
    String generateOtp(String type, String email);

    /**
     * Verify OTP: đúng thì xóa khỏi Redis (dùng 1 lần), sai/hết hạn thì throw.
     * Chống brute-force: nhập sai quá maxVerifyAttempts lần thì vô hiệu hóa OTP hiện tại,
     * buộc phải yêu cầu mã mới.
     */
    void verifyOtp(String type, String email, String otp);
}
