package com.sba.lexilearnbe.modules.auth.event;

/**
 * Event yêu cầu gửi mail OTP, được publish từ AuthService.
 * Mail chỉ được gửi SAU KHI transaction DB commit thành công
 * (xem {@link OtpEmailListener}) — tránh giữ DB connection trong lúc chờ SMTP.
 */
public record OtpEmailEvent(String email, String otp, long ttlMinutes) {
}
