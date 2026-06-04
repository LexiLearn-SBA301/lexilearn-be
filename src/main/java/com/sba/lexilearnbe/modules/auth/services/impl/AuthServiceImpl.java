package com.sba.lexilearnbe.modules.auth.services.impl;

import com.sba.lexilearnbe.modules.auth.dto.request.LoginRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.RefreshTokenRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.RegisterRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.ResendOtpRequest;
import com.sba.lexilearnbe.modules.auth.dto.request.VerifyOtpRequest;
import com.sba.lexilearnbe.modules.auth.dto.response.TokenResponse;
import com.sba.lexilearnbe.modules.auth.entity.Account;
import com.sba.lexilearnbe.modules.auth.entity.Role;
import com.sba.lexilearnbe.modules.auth.enums.AccountStatus;
import com.sba.lexilearnbe.modules.auth.event.OtpEmailEvent;
import com.sba.lexilearnbe.modules.auth.repository.AccountRepository;
import com.sba.lexilearnbe.modules.auth.repository.RoleRepository;
import com.sba.lexilearnbe.modules.auth.services.AuthService;
import com.sba.lexilearnbe.modules.auth.services.OtpService;
import com.sba.lexilearnbe.modules.auth.services.RefreshTokenService;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import com.sba.lexilearnbe.shared.infrastructure.caches.keys.RedisKeys;
import com.sba.lexilearnbe.shared.infrastructure.security.JwtService;
import com.sba.lexilearnbe.shared.infrastructure.security.TokenBlacklistService;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE = "USER";
    private static final String BEARER_PREFIX = "Bearer ";

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    /**
     * Đăng ký account mới:
     * 1. Lowercase email (unique index trên LOWER(email))
     * 2. Email đã tồn tại:
     *    - Đã verify (ACTIVE/LOCKED) → báo lỗi ACCOUNT_EXISTS
     *    - Chưa verify (UNVERIFIED) → cho đăng ký lại: cập nhật password mới
     *      (an toàn vì email chưa verify thì chưa ai "sở hữu" account)
     * 3. Tạo account UNVERIFIED + gán role USER
     * 4. Sinh OTP → Redis → gửi mail xác thực
     */
    @Override
    @Transactional
    public void register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        Account existing = accountRepository.findByEmail(email).orElse(null);
        if (existing != null) {
            if (existing.getStatus() != AccountStatus.UNVERIFIED) {
                throw new ApiException(ErrorCode.ACCOUNT_EXISTS);
            }

            // Chưa verify → coi như đăng ký lại từ đầu
            existing.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            accountRepository.save(existing);

            sendRegisterOtp(email);
            log.info("Đăng ký lại account chưa xác thực: {}", email);
            return;
        }

        Role userRole = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow(() -> new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "Role USER chưa được seed trong DB"));

        Account account = Account.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status(AccountStatus.UNVERIFIED) // set rõ trong code, không dựa vào DB default
                .roles(Set.of(userRole))
                .build();

        accountRepository.save(account);

        sendRegisterOtp(email);
        log.info("Đăng ký account mới: {}", email);
    }

    /**
     * Xác thực email bằng OTP:
     * 1. Tìm account theo email (lowercase)
     * 2. Chặn account đã ACTIVE / bị LOCKED
     * 3. Verify OTP (sai/hết hạn/quá số lần thử → OtpService throw)
     * 4. Đổi status → ACTIVE, ghi nhận thời điểm xác thực
     */
    @Override
    @Transactional
    public void verifyRegisterOtp(VerifyOtpRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateUnverified(account);

        otpService.verifyOtp(OTP_TYPE_REGISTER, email, request.getOtp());

        account.setStatus(AccountStatus.ACTIVE);
        account.setEmailVerifiedAt(LocalDateTime.now());
        accountRepository.save(account);

        log.info("Xác thực email thành công: {}", email);
    }

    /**
     * Gửi lại OTP xác thực email (OTP cũ hết hạn / thất lạc).
     * Không @Transactional: chỉ thao tác Redis + gửi mail, không ghi DB.
     * Rate limit gửi mail do OtpService đảm nhận.
     */
    @Override
    public void resendRegisterOtp(ResendOtpRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateUnverified(account);

        sendRegisterOtp(email);
        log.info("Gửi lại OTP xác thực email: {}", email);
    }

    /**
     * Đăng nhập:
     * 1. Tìm account theo email + so khớp password (sai email hay sai password
     *    đều trả INVALID_CREDENTIALS — không để lộ email nào đã đăng ký)
     * 2. Chặn account chưa verify / bị khóa
     * 3. Cấp access token (JWT) + refresh token (Redis)
     */
    @Override
    public TokenResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        // findWithRolesByEmail: fetch kèm roles (LAZY) vì sinh JWT cần roles
        Account account = accountRepository.findWithRolesByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            throw new ApiException(ErrorCode.INVALID_CREDENTIALS);
        }

        validateActive(account);

        log.info("Đăng nhập thành công: {}", email);
        return issueTokens(account);
    }

    /**
     * Refresh token rotation + reuse detection:
     * 1. Rotate: token cũ chuyển thành "đã dùng", cấp token mới cùng family.
     *    Token "đã dùng" bị dùng lại → coi như bị trộm, RefreshTokenService
     *    revoke toàn bộ family và throw TOKEN_INVALID
     * 2. Kiểm tra account vẫn còn hợp lệ — nếu không thì thu hồi luôn
     *    token vừa cấp (kill cả phiên đăng nhập)
     */
    @Override
    public TokenResponse refresh(RefreshTokenRequest request) {
        RefreshTokenService.RotatedToken rotated = refreshTokenService.rotate(request.getRefreshToken());

        try {
            // findWithRolesById: fetch kèm roles (LAZY) vì sinh JWT cần roles
            Account account = accountRepository.findWithRolesById(rotated.accountId())
                    .orElseThrow(() -> new ApiException(ErrorCode.ACCOUNT_NOT_FOUND));

            validateActive(account);

            log.info("Refresh token thành công: {}", account.getEmail());
            return TokenResponse.builder()
                    .accessToken(jwtService.generateAccessToken(account))
                    .refreshToken(rotated.newToken())
                    .expiresIn(jwtService.getAccessTokenTtl())
                    .build();
        } catch (ApiException ex) {
            // Account bị khóa / không còn tồn tại → token mới vừa cấp cũng phải chết theo
            refreshTokenService.revoke(rotated.newToken());
            throw ex;
        }
    }

    /**
     * Đăng xuất: thu hồi refresh token + toàn bộ family của nó (idempotent),
     * đồng thời blacklist access token đến khi hết hạn — sau logout,
     * access token (kể cả khi đã bị đánh cắp) không còn dùng được.
     */
    @Override
    public void logout(RefreshTokenRequest request, String authorizationHeader) {
        refreshTokenService.revoke(request.getRefreshToken());
        blacklistAccessToken(authorizationHeader);
        log.info("Đăng xuất: refresh token đã thu hồi, access token đã blacklist");
    }

    /**
     * Blacklist access token lấy từ header Authorization.
     * Header thiếu / token hết hạn / không hợp lệ → bỏ qua (token đã chết
     * thì không còn gì để thu hồi) — không được làm fail luồng logout.
     */
    private void blacklistAccessToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            return;
        }
        try {
            tokenBlacklistService.blacklist(authorizationHeader.substring(BEARER_PREFIX.length()));
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("Bỏ qua blacklist access token khi logout (token hết hạn/không hợp lệ): {}", ex.getMessage());
        }
    }

    /** Cấp cặp access token + refresh token cho account. */
    private TokenResponse issueTokens(Account account) {
        return TokenResponse.builder()
                .accessToken(jwtService.generateAccessToken(account))
                .refreshToken(refreshTokenService.issue(account.getId()))
                .expiresIn(jwtService.getAccessTokenTtl())
                .build();
    }

    /** Account phải ACTIVE mới được đăng nhập / refresh token. */
    private void validateActive(Account account) {
        if (account.getStatus() == AccountStatus.UNVERIFIED) {
            throw new ApiException(ErrorCode.ACCOUNT_NOT_VERIFIED);
        }
        if (account.getStatus() == AccountStatus.LOCKED) {
            throw new ApiException(ErrorCode.ACCOUNT_LOCKED);
        }
    }

    /**
     * Sinh OTP đăng ký (đã gồm rate limit — lỗi rate limit throw ngay tại đây
     * để user nhận được response lỗi), rồi publish event gửi mail.
     * Mail được gửi bất đồng bộ SAU KHI transaction commit (OtpEmailListener)
     * — không giữ DB connection trong lúc chờ SMTP.
     */
    private void sendRegisterOtp(String email) {
        String otp = otpService.generateOtp(OTP_TYPE_REGISTER, email);
        eventPublisher.publishEvent(new OtpEmailEvent(email, otp, RedisKeys.TTL_OTP / 60));
    }

    /** Account phải đang UNVERIFIED mới được verify/resend OTP đăng ký. */
    private void validateUnverified(Account account) {
        if (account.getStatus() == AccountStatus.ACTIVE) {
            throw new ApiException(ErrorCode.ACCOUNT_ALREADY_VERIFIED);
        }
        if (account.getStatus() == AccountStatus.LOCKED) {
            throw new ApiException(ErrorCode.ACCOUNT_LOCKED);
        }
    }
}
