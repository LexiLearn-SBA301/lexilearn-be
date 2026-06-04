package com.sba.lexilearnbe.shared.infrastructure.security;

import com.sba.lexilearnbe.modules.auth.entity.Account;
import com.sba.lexilearnbe.modules.auth.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Sinh và parse JWT access token (HS256).
 * Access token là stateless: chứa sẵn accountId + email + roles trong claims,
 * mỗi request không cần query DB để biết user là ai.
 */
@Component
public class JwtService {

    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_ROLES = "roles";

    @Value("${app.jwt.secret}")
    private String secret;

    /** Thời gian sống của access token (giây). */
    @Getter
    @Value("${app.jwt.access-token-ttl:1800}")
    private long accessTokenTtl;

    private SecretKey key;

    @PostConstruct
    void init() {
        // HS256 yêu cầu key >= 32 bytes, Keys.hmacShaKeyFor sẽ throw nếu secret quá ngắn
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** Sinh access token: sub = accountId, kèm email + danh sách role name. */
    public String generateAccessToken(Account account) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenTtl * 1000);

        List<String> roleNames = account.getRoles().stream()
                .map(Role::getName)
                .toList();

        return Jwts.builder()
                .subject(account.getId().toString())
                .claim(CLAIM_EMAIL, account.getEmail())
                .claim(CLAIM_ROLES, roleNames)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /**
     * Parse và verify access token, trả về claims.
     * Token sai chữ ký / sai format → JwtException, hết hạn → ExpiredJwtException.
     */
    public Claims parseAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
