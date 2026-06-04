package com.sba.lexilearnbe.shared.infrastructure.security;

import com.sba.lexilearnbe.shared.infrastructure.caches.helpers.RedisSupported;
import com.sba.lexilearnbe.shared.infrastructure.caches.keys.RedisKeys;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Blacklist access token khi logout — vá giới hạn "JWT stateless không revoke được":
 * token bị blacklist trong Redis với TTL = thời gian sống còn lại của token,
 * nên blacklist tự dọn rác khi token hết hạn tự nhiên (không phình vô hạn).
 *
 * Chỉ lưu SHA-256 hash của token (như refresh token): lỡ Redis bị dump
 * cũng không lộ token dùng được.
 */
@Component
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final RedisSupported redis;
    private final JwtService jwtService;

    /**
     * Đưa access token vào blacklist đến khi nó hết hạn.
     * Token sai chữ ký / hết hạn → JwtService throw (caller tự quyết định bỏ qua:
     * token đã chết thì không còn gì để thu hồi).
     */
    public void blacklist(String token) {
        Claims claims = jwtService.parseAccessToken(token);

        // TTL còn lại của token (làm tròn lên 1 giây để không hụt phần lẻ ms)
        long remainingMs = claims.getExpiration().getTime() - System.currentTimeMillis();
        long ttlSeconds = (remainingMs + 999) / 1000;
        if (ttlSeconds <= 0) {
            return; // token đã hết hạn — filter tự chặn, không cần blacklist
        }

        redis.set(RedisKeys.accessTokenBlacklistKey(sha256(token)), "1", ttlSeconds);
    }

    /** Token có đang nằm trong blacklist không (đã logout). */
    public boolean isBlacklisted(String token) {
        return redis.exists(RedisKeys.accessTokenBlacklistKey(sha256(token)));
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 luôn có sẵn trong JDK, không bao giờ xảy ra
            throw new IllegalStateException("SHA-256 không khả dụng", e);
        }
    }
}
