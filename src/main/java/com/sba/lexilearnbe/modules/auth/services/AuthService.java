package com.sba.lexilearnbe.modules.auth.services;

import com.sba.lexilearnbe.modules.auth.dto.request.RegisterRequest;
import com.sba.lexilearnbe.modules.auth.entity.Account;
import com.sba.lexilearnbe.modules.auth.entity.Role;
import com.sba.lexilearnbe.modules.auth.enums.AccountStatus;
import com.sba.lexilearnbe.modules.auth.repository.AccountRepository;
import com.sba.lexilearnbe.modules.auth.repository.RoleRepository;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import com.sba.lexilearnbe.shared.infrastructure.caches.keys.RedisKeys;
import com.sba.lexilearnbe.shared.infrastructure.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    public static final String OTP_TYPE_REGISTER = "register";
    private static final String DEFAULT_ROLE = "USER";

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final MailService mailService;

    /**
     * Đăng ký account mới:
     * 1. Lowercase email (unique index trên LOWER(email))
     * 2. Check email đã tồn tại chưa
     * 3. Tạo account UNVERIFIED + gán role USER
     * 4. Sinh OTP → Redis → gửi mail xác thực
     */
    @Transactional
    public void register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (accountRepository.existsByEmail(email)) {
            throw new ApiException(ErrorCode.ACCOUNT_EXISTS);
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

        // Gửi OTP xác thực email
        String otp = otpService.generateOtp(OTP_TYPE_REGISTER, email);
        mailService.sendOtpEmail(email, otp, RedisKeys.TTL_OTP / 60);

        log.info("Đăng ký account mới: {}", email);
    }
}
