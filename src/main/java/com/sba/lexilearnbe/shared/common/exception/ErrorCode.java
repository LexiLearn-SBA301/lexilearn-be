package com.sba.lexilearnbe.shared.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // SUCCESS CODE
    SUCCESS("success_request", HttpStatus.OK, "Request successful"),

    // ── System ──────────────────────────────────────────────────
    INTERNAL_SERVER_ERROR("internal_server_error", HttpStatus.INTERNAL_SERVER_ERROR, "Lỗi hệ thống"),
    VALIDATION_ERROR     ("validation_error",      HttpStatus.BAD_REQUEST,           "Dữ liệu không hợp lệ"),
    INVALID_FORMAT       ("invalid_format",        HttpStatus.BAD_REQUEST,           "Sai định dạng JSON"),
    API_NOT_FOUND        ("api_not_found",         HttpStatus.NOT_FOUND,             "Endpoint không tồn tại"),
    METHOD_NOT_ALLOWED   ("method_not_allowed",    HttpStatus.METHOD_NOT_ALLOWED,    "HTTP method không hỗ trợ"),
    TOO_MANY_REQUESTS    ("too_many_requests",     HttpStatus.TOO_MANY_REQUESTS,     "Quá nhiều yêu cầu"),
    RESOURCE_NOT_FOUND      ("resource_not_found",    HttpStatus.NOT_FOUND,             "Tài nguyên không tồn tại"),

    // ── Auth ────────────────────────────────────────────────────
    UNAUTHENTICATED      ("unauthenticated",       HttpStatus.UNAUTHORIZED,  "Chưa đăng nhập"),
    FORBIDDEN            ("forbidden",             HttpStatus.FORBIDDEN,     "Không có quyền"),
    TOKEN_INVALID        ("token_invalid",         HttpStatus.UNAUTHORIZED,  "Token không hợp lệ"),
    TOKEN_EXPIRED        ("token_expired",         HttpStatus.UNAUTHORIZED,  "Token hết hạn"),

    // ── Account ─────────────────────────────────────────────────
    ACCOUNT_NOT_FOUND    ("account_not_found",     HttpStatus.NOT_FOUND, "Tài khoản không tồn tại"),
    ACCOUNT_EXISTS       ("account_exists",        HttpStatus.CONFLICT,  "Email đã được đăng ký"),
    ACCOUNT_LOCKED       ("account_locked",        HttpStatus.FORBIDDEN, "Tài khoản bị khóa"),
    ACCOUNT_NOT_VERIFIED ("account_not_verified",  HttpStatus.FORBIDDEN, "Email chưa được xác thực"),
    OTP_INVALID          ("otp_invalid",           HttpStatus.BAD_REQUEST, "Mã OTP không đúng"),
    OTP_EXPIRED          ("otp_expired",           HttpStatus.BAD_REQUEST, "Mã OTP đã hết hạn"),

    // ── Property ────────────────────────────────────────────────
    PROPERTY_NOT_FOUND   ("property_not_found",    HttpStatus.NOT_FOUND, "Property không tồn tại"),
    ROOM_NOT_FOUND       ("room_not_found",        HttpStatus.NOT_FOUND, "Phòng không tồn tại"),
    ROOM_NOT_AVAILABLE   ("room_not_available",    HttpStatus.CONFLICT,  "Phòng không khả dụng"),

    // ── Booking ─────────────────────────────────────────────────
    BOOKING_NOT_FOUND         ("booking_not_found",         HttpStatus.NOT_FOUND, "Booking không tồn tại"),
    BOOKING_ALREADY_CANCELLED ("booking_already_cancelled", HttpStatus.CONFLICT, "Booking đã bị hủy"),
    BOOKING_CANNOT_CANCEL     ("booking_cannot_cancel",     HttpStatus.BAD_REQUEST, "Không thể hủy booking"),

    // ── Payment ─────────────────────────────────────────────────
    PAYMENT_NOT_FOUND    ("payment_not_found",     HttpStatus.NOT_FOUND,    "Thông tin thanh toán không tồn tại"),
    PAYMENT_FAILED       ("payment_failed",        HttpStatus.BAD_REQUEST,  "Thanh toán thất bại"),
    REFUND_NOT_ALLOWED   ("refund_not_allowed",    HttpStatus.FORBIDDEN,    "Không có quyền hoàn tiền"),

    // ── Ticket ──────────────────────────────────────────────────
    TICKET_NOT_FOUND        ("ticket_not_found",        HttpStatus.NOT_FOUND, "Ticket không tồn tại"),
    TICKET_ALREADY_RESOLVED ("ticket_already_resolved", HttpStatus.CONFLICT, "Ticket đã được giải quyết"),

    // ── S3 ──────────────────────────────────────────────────
    FAIL_TO_UPLOAD          ("fail_to_upload", HttpStatus.BAD_REQUEST, "Không thể tải lên file"),
    FAIL_TO_GENERATED_PRESIGN_KEY ("fail_to_generate_prKey", HttpStatus.BAD_REQUEST, "Không thể tạo presigned URL"),;


    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}