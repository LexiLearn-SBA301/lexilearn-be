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

    @Value("${app.otp.max-verify-attempts:5}")
    private long maxVerifyAttempts;

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

        // 3. Lưu Redis với TTL — OTP mới ghi đè OTP cũ cùng type,
        //    đồng thời reset bộ đếm nhập sai (OTP mới = số lần thử mới)
        redis.set(RedisKeys.otpCodeKey(type, email), otp.toString(), RedisKeys.TTL_OTP);
        redis.delete(RedisKeys.otpVerifyAttemptKey(type, email));

        return otp.toString();
    }

    /**
     * Verify OTP: đúng thì xóa khỏi Redis (dùng 1 lần), sai/hết hạn thì throw.
     * Chống brute-force: nhập sai quá maxVerifyAttempts lần thì vô hiệu hóa OTP hiện tại,
     * buộc phải yêu cầu mã mới.
     */
    public void verifyOtp(String type, String email, String otp) {
        String key = RedisKeys.otpCodeKey(type, email);
        String attemptKey = RedisKeys.otpVerifyAttemptKey(type, email);

        // 1. Đã nhập sai quá số lần cho phép → chặn luôn, không cho thử tiếp
        if (redis.getCounter(attemptKey) >= maxVerifyAttempts) {
            throw new ApiException(ErrorCode.TOO_MANY_REQUESTS, "Bạn đã nhập sai OTP quá nhiều lần, vui lòng yêu cầu mã mới");
        }

        String stored = redis.get(key);
        if (stored == null) {
            throw new ApiException(ErrorCode.OTP_EXPIRED);
        }

        // 2. Nhập sai → đếm số lần sai, chạm ngưỡng thì vô hiệu hóa OTP
        if (!stored.equals(otp)) {
            long failCount = redis.increment(attemptKey, RedisKeys.TTL_OTP);
            if (failCount >= maxVerifyAttempts) {
                redis.delete(key);
                throw new ApiException(ErrorCode.TOO_MANY_REQUESTS, "Bạn đã nhập sai OTP quá nhiều lần, vui lòng yêu cầu mã mới");
            }
            throw new ApiException(ErrorCode.OTP_INVALID);
        }

        // 3. Đúng → OTP dùng 1 lần, xóa cả mã lẫn bộ đếm
        redis.delete(key);
        redis.delete(attemptKey);
    }
}
