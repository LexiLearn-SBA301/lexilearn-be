package com.sba.lexilearnbe.shared.infrastructure.mail;

import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    /**
     * Gửi mail OTP dạng HTML (template Thymeleaf: templates/mail/otp-email.html)
     */
    public void sendOtpEmail(String to, String otp, long ttlMinutes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Mã OTP xác thực email");

            Context context = new Context();
            context.setVariable("otp", otp);
            context.setVariable("ttlMinutes", ttlMinutes);

            String html = templateEngine.process("mail/otp-email", context);
            helper.setText(html, true); // true = nội dung HTML

            mailSender.send(message);
            log.info("Đã gửi OTP email tới {}", to);
        } catch (MessagingException e) {
            log.error("Gửi OTP email thất bại tới {}: ", to, e);
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "Không thể gửi email xác thực");
        }
    }
}
