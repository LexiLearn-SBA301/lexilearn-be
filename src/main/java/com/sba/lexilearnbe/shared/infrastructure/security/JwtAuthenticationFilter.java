package com.sba.lexilearnbe.shared.infrastructure.security;

import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Filter xác thực JWT: đọc access token từ header Authorization (Bearer),
 * verify và set Authentication vào SecurityContext.
 *
 * - Không có token → đi tiếp, AuthenticationEntryPoint sẽ chặn nếu endpoint cần đăng nhập
 * - Token hết hạn / không hợp lệ → trả 401 JSON ngay tại filter
 *   (phân biệt TOKEN_EXPIRED vs TOKEN_INVALID để FE biết khi nào cần refresh)
 * - Token nằm trong blacklist (đã logout) → trả 401 TOKEN_INVALID
 * - Bỏ qua các endpoint public (đặc biệt /auth/refresh: FE thường gắn sẵn
 *   access token đã hết hạn vào header, không được chặn ở đây)
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final SecurityErrorResponseWriter errorWriter;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        boolean publicEndpoint = Arrays.stream(SecurityConfig.PUBLIC_ENDPOINTS)
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, request.getRequestURI()));
        boolean publicGetEndpoint = "GET".equalsIgnoreCase(request.getMethod())
                && Arrays.stream(SecurityConfig.PUBLIC_GET_ENDPOINTS)
                .anyMatch(pattern -> PATH_MATCHER.match(
                        pattern,
                        request.getRequestURI()
                ));
        return publicEndpoint || publicGetEndpoint;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(BEARER_PREFIX.length());

        try {
            Claims claims = jwtService.parseAccessToken(token);

            // Token hợp lệ về chữ ký/hạn nhưng đã bị thu hồi khi logout → chặn
            if (tokenBlacklistService.isBlacklisted(token)) {
                errorWriter.write(request, response, ErrorCode.TOKEN_INVALID);
                return;
            }

            UUID accountId = UUID.fromString(claims.getSubject());

            // Role trong DB không có prefix ROLE_, thêm ở đây để hasRole(...) hoạt động đúng
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get(JwtService.CLAIM_ROLES, List.class);
            List<SimpleGrantedAuthority> authorities = roles == null ? List.of()
                    : roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList();

            var authentication = new UsernamePasswordAuthenticationToken(accountId, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException ex) {
            errorWriter.write(request, response, ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException ex) {
            errorWriter.write(request, response, ErrorCode.TOKEN_INVALID);
        }
    }
}
