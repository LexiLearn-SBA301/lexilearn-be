package com.sba.lexilearnbe.modules.auth.services.impl;

import com.sba.lexilearnbe.modules.auth.services.RefreshTokenService;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import com.sba.lexilearnbe.shared.infrastructure.caches.helpers.RedisSupported;
import com.sba.lexilearnbe.shared.infrastructure.caches.keys.RedisKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

/**
 * Refresh token rotation + reuse detection (token family) theo OAuth 2.0 Security BCP (RFC 9700).
 *
 * Cấu trúc Redis:
 * - refreshToken:&lt;hash&gt;        → "accountId:familyId"  (token đang sống)
 * - refreshToken:used:&lt;hash&gt;   → familyId             (token đã rotate, giữ lại để phát hiện replay)
 * - refreshToken:family:&lt;id&gt;   → hash token đang sống (mỗi family chỉ có đúng 1 token sống)
 *
 * Token "đã dùng" mà bị dùng lại → chắc chắn có 2 bên cùng giữ token (bị trộm)
 * → revoke cả family, cả 2 bên phải đăng nhập lại bằng password.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32; // 256 bit entropy
    private static final String VALUE_SEPARATOR = ":"; // UUID không chứa ':' nên dùng làm separator an toàn

    private final RedisSupported redis;

    @Override
    public String issue(UUID accountId) {
        // Login mới = family mới
        return issueInFamily(accountId.toString(), UUID.randomUUID().toString());
    }

    @Override
    public RotatedToken rotate(String rawToken) {
        String hash = sha256(rawToken);
        String value = redis.get(RedisKeys.refreshTokenKey(hash));

        if (value == null) {
            detectReuse(hash); // token "đã dùng" bị replay → revoke family + throw
            throw new ApiException(ErrorCode.TOKEN_INVALID, "Refresh token không hợp lệ hoặc đã hết hạn");
        }

        String[] parts = value.split(VALUE_SEPARATOR);
        if (parts.length != 2) {
            // Dữ liệu format cũ / hỏng → không tin, bắt đăng nhập lại
            redis.delete(RedisKeys.refreshTokenKey(hash));
            throw new ApiException(ErrorCode.TOKEN_INVALID, "Refresh token không hợp lệ hoặc đã hết hạn");
        }
        String accountId = parts[0];
        String familyId = parts[1];

        // Rotate: token cũ chuyển sang "đã dùng" (không xóa hẳn — giữ dấu vết để phát hiện replay)
        redis.delete(RedisKeys.refreshTokenKey(hash));
        redis.set(RedisKeys.refreshTokenUsedKey(hash), familyId, RedisKeys.TTL_REFRESH_TOKEN);

        String newToken = issueInFamily(accountId, familyId);
        return new RotatedToken(UUID.fromString(accountId), newToken);
    }

    @Override
    public void revoke(String rawToken) {
        String hash = sha256(rawToken);
        String value = redis.get(RedisKeys.refreshTokenKey(hash));

        if (value != null) {
            String[] parts = value.split(VALUE_SEPARATOR);
            if (parts.length == 2) {
                revokeFamily(parts[1]); // logout = kết thúc cả phiên đăng nhập
            }
        }
        redis.delete(RedisKeys.refreshTokenKey(hash));
    }

    /** Sinh token mới trong family: lưu hash → "accountId:familyId", cập nhật token sống của family. */
    private String issueInFamily(String accountId, String familyId) {
        byte[] bytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        String hash = sha256(rawToken);

        // Chỉ lưu hash: lỡ Redis bị dump cũng không lộ token dùng được
        redis.set(RedisKeys.refreshTokenKey(hash), accountId + VALUE_SEPARATOR + familyId, RedisKeys.TTL_REFRESH_TOKEN);
        redis.set(RedisKeys.refreshTokenFamilyKey(familyId), hash, RedisKeys.TTL_REFRESH_TOKEN);

        return rawToken;
    }

    /**
     * Token không còn sống — kiểm tra có phải token "đã dùng" bị replay không.
     * Nếu đúng → có 2 bên cùng giữ token (bị trộm) → revoke toàn bộ family.
     */
    private void detectReuse(String hash) {
        String familyId = redis.get(RedisKeys.refreshTokenUsedKey(hash));
        if (familyId == null) {
            return; // token chưa từng tồn tại / đã hết hạn tự nhiên — không phải replay
        }

        revokeFamily(familyId);
        log.warn("Phát hiện refresh token bị dùng lại — đã revoke toàn bộ family {}", familyId);
        throw new ApiException(ErrorCode.TOKEN_INVALID,
                "Phát hiện refresh token bị dùng lại. Toàn bộ phiên liên quan đã bị thu hồi, vui lòng đăng nhập lại");
    }

    /** Revoke cả family: xóa token đang sống + key family. */
    private void revokeFamily(String familyId) {
        String activeHash = redis.get(RedisKeys.refreshTokenFamilyKey(familyId));
        if (activeHash != null) {
            redis.delete(RedisKeys.refreshTokenKey(activeHash));
        }
        redis.delete(RedisKeys.refreshTokenFamilyKey(familyId));
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
