package com.sba.lexilearnbe.modules.auth.event;

import com.sba.lexilearnbe.shared.infrastructure.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Gửi mail OTP bất đồng bộ, tách khỏi transaction DB:
 * - AFTER_COMMIT: chỉ gửi khi account đã được lưu thành công
 *   (transaction rollback → không gửi mail mồ côi)
 * - fallbackExecution = true: resend OTP không chạy trong transaction
 *   (chỉ thao tác Redis) thì vẫn gửi mail bình thường
 * - @Async: SMTP I/O chạy ở thread riêng, không giữ DB connection
 *   và không bắt request chờ mail server
 *
 * Trade-off: gửi mail fail thì user vẫn nhận response thành công
 * (lỗi chỉ được log) — user dùng /resend-otp làm lưới đỡ.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OtpEmailListener {

    private final MailService mailService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handle(OtpEmailEvent event) {
        try {
            mailService.sendOtpEmail(event.email(), event.otp(), event.ttlMinutes());
        } catch (Exception e) {
            // Không re-throw: exception trong async listener không tới được user,
            // chỉ log để theo dõi (MailService cũng đã log chi tiết)
            log.error("Gửi OTP email bất đồng bộ thất bại tới {}", event.email(), e);
        }
    }
}
