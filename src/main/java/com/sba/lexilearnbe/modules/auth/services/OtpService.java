package com.sba.lexilearnbe.modules.auth.services;

import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import com.sba.lexilearnbe.shared.infrastructure.caches.helpers.RedisSupported;
import com.sba.lexilearnbe.shared.infrastructure.caches.keys.RedisKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class OtpService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final RedisSupported redis;

    @Value("${app.otp.length:6}")
    private int otpLength;

    @Value("${app.otp.max-sends-per-hour:5}")
    private long maxSendsPerHour;

    /**
     * Sinh OTP, lưu vào Redis và check rate limit (chống spam gửi mail).
     *
     * @param type loại OTP, ví dụ "register", "reset_password"
     * @return mã OTP vừa sinh
     */
    public String generateOtp(String type, String email) {
        // 1. Rate limit: tối đa N lần gửi / giờ / email
        long sendCount = redis.increment(RedisKeys.otpRateLimitKey(email), RedisKeys.TTL_OTP_RATE_LIMIT);
        if (sendCount > maxSendsPerHour) {
            throw new ApiException(ErrorCode.TOO_MANY_REQUESTS, "Bạn đã yêu cầu OTP quá nhiều lần, thử lại sau 1 giờ");
        }

        // 2. Sinh OTP ngẫu nhiên (SecureRandom, đủ otpLength chữ số)
        StringBuilder otp = new StringBuilder(otpLength);
        for (int i = 0; i < otpLength; i++) {
            otp.append(RANDOM.nextInt(10));
        }

        // 3. Lưu Redis với TTL — OTP mới ghi đè OTP cũ cùng type
        redis.set(RedisKeys.otpCodeKey(type, email), otp.toString(), RedisKeys.TTL_OTP);

        return otp.toString();
    }

    /**
     * Verify OTP: đúng thì xóa khỏi Redis (dùng 1 lần), sai/hết hạn thì throw.
     */
    public void verifyOtp(String type, String email, String otp) {
        String key = RedisKeys.otpCodeKey(type, email);
        String stored = redis.get(key);

        if (stored == null) {
            throw new ApiException(ErrorCode.OTP_EXPIRED);
        }
        if (!stored.equals(otp)) {
            throw new ApiException(ErrorCode.OTP_INVALID);
        }

        redis.delete(key);
    }
}
