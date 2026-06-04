package com.sba.lexilearnbe.shared.infrastructure.caches.keys;

import java.util.UUID;

public class RedisKeys {

    private RedisKeys(){}

//    Key for permissions and roles of an account
//    public static String permissionKey(UUID accountId) {
//        return "permissions:" + accountId;
//    }

//    public static String rolesKey(UUID accountId) {
//        return "roles:" + accountId;
//    }

//    Key for refreshToken
    public static String refreshTokenKey(String tokenHashed) {
        return "refreshToken:" + tokenHashed;
    }

//    Key for OTP code
    public static String otpCodeKey(String type, String email) {
        return "otp: "+ type.toLowerCase()  +": " + email.toLowerCase();
    }

//    counter for OTP code generation to prevent abuse
    public static String otpRateLimitKey(String email) {
        return "rate:otp:" + email.toLowerCase();
    }

//    counter for failed OTP verify attempts to prevent brute-force
    public static String otpVerifyAttemptKey(String type, String email) {
        return "rate:otp-verify:" + type.toLowerCase() + ":" + email.toLowerCase();
    }

    public static final long TTL_PERMISSION_ROLE = 15 * 60L;        // 15 phút
    public static final long TTL_REFRESH_TOKEN   = 30 * 24 * 3600L; // 30 ngày
    public static final long TTL_OTP             = 5 * 60L;         // 5 phút
    public static final long TTL_OTP_RESET_PWD   = 3 * 60L;         // 3 phút
    public static final long TTL_OTP_RATE_LIMIT  = 60 * 60L;        // 1 giờ
    public static final long TTL_AVAILABILITY    = 5 * 60L;         // 5 phút

}
