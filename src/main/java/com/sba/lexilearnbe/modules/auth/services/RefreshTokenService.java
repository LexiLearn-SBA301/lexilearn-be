package com.sba.lexilearnbe.modules.auth.services;

import java.util.UUID;

public interface RefreshTokenService {

    /** Kết quả rotate: accountId của chủ token + refresh token mới (cùng family với token cũ). */
    record RotatedToken(UUID accountId, String newToken) {}

    /**
     * Phát hành refresh token mới cho account (dùng khi login):
     * tạo family mới, sinh token ngẫu nhiên, lưu SHA-256 hash vào Redis (TTL 30 ngày).
     * 1 family = 1 phiên đăng nhập; các token sinh ra từ refresh đều thuộc cùng family.
     *
     * @return raw token trả về cho client (Redis chỉ giữ hash, không giữ raw)
     */
    String issue(UUID accountId);

    /**
     * Rotate refresh token (dùng khi refresh):
     * - Token đang sống → đánh dấu "đã dùng", cấp token mới cùng family
     * - Token "đã dùng" bị dùng lại → phát hiện bị trộm, revoke toàn bộ family
     *   (cả attacker lẫn user thật đều phải đăng nhập lại) → throw TOKEN_INVALID
     * - Token không tồn tại (sai / hết hạn) → throw TOKEN_INVALID
     */
    RotatedToken rotate(String rawToken);

    /**
     * Thu hồi refresh token và toàn bộ family của nó (dùng khi logout).
     * Idempotent — token không tồn tại cũng không lỗi.
     */
    void revoke(String rawToken);
}
