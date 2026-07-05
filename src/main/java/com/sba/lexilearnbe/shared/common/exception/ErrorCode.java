package com.sba.lexilearnbe.shared.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

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
    INVALID_CREDENTIALS  ("invalid_credentials",   HttpStatus.UNAUTHORIZED,  "Email hoặc mật khẩu không đúng"),
    FORBIDDEN            ("forbidden",             HttpStatus.FORBIDDEN,     "Không có quyền"),
    TOKEN_INVALID        ("token_invalid",         HttpStatus.UNAUTHORIZED,  "Token không hợp lệ"),
    TOKEN_EXPIRED        ("token_expired",         HttpStatus.UNAUTHORIZED,  "Token hết hạn"),

    // ── Account ─────────────────────────────────────────────────
    ACCOUNT_NOT_FOUND    ("account_not_found",     HttpStatus.NOT_FOUND, "Tài khoản không tồn tại"),
    ACCOUNT_EXISTS       ("account_exists",        HttpStatus.CONFLICT,  "Email đã được đăng ký"),
    ACCOUNT_LOCKED       ("account_locked",        HttpStatus.FORBIDDEN, "Tài khoản bị khóa"),
    ACCOUNT_NOT_VERIFIED ("account_not_verified",  HttpStatus.FORBIDDEN, "Email chưa được xác thực"),
    ACCOUNT_ALREADY_VERIFIED("account_already_verified", HttpStatus.CONFLICT, "Email đã được xác thực"),
    OTP_INVALID          ("otp_invalid",           HttpStatus.BAD_REQUEST, "Mã OTP không đúng"),
    OTP_EXPIRED          ("otp_expired",           HttpStatus.BAD_REQUEST, "Mã OTP đã hết hạn"),

    // ── Work ────────────────────────────────────────────────────
    WORK_NOT_FOUND       ("work_not_found",        HttpStatus.NOT_FOUND, "Tác phẩm không tồn tại"),
    WORK_ALREADY_EXISTS  ("work_already_exists",   HttpStatus.CONFLICT,  "Tác phẩm đã tồn tại"),

    // ── Author ──────────────────────────────────────────────────
    AUTHOR_NOT_FOUND     ("author_not_found",      HttpStatus.NOT_FOUND, "Tác giả không tồn tại"),
    AUTHOR_ALREADY_EXISTS("author_already_exists", HttpStatus.CONFLICT,  "Tác giả đã tồn tại"),

    // ── Tag ─────────────────────────────────────────────────────
    TAG_NOT_FOUND        ("tag_not_found",         HttpStatus.NOT_FOUND, "Tag không tồn tại"),
    TAG_ALREADY_EXISTS   ("tag_already_exists",    HttpStatus.CONFLICT,  "Tag đã tồn tại"),

    // ── Work Section ────────────────────────────────────────────
    SECTION_NOT_FOUND    ("section_not_found",     HttpStatus.NOT_FOUND, "Phần văn bản không tồn tại"),

    // ── Work Commentary ─────────────────────────────────────────
    COMMENTARY_NOT_FOUND ("commentary_not_found",  HttpStatus.NOT_FOUND, "Bình phẩm không tồn tại"),

    // ── Work Review ──────────────────────────────────────────────
    REVIEW_NOT_FOUND          ("review_not_found",          HttpStatus.NOT_FOUND, "Bình phẩm của độc giả không tồn tại"),
    REVIEW_ALREADY_EXISTS     ("review_already_exists",     HttpStatus.CONFLICT, "Bạn đã có bình phẩm cho tác phẩm này"),
    REVIEW_REVISION_NOT_FOUND ("review_revision_not_found", HttpStatus.NOT_FOUND, "Phiên bản bình phẩm không tồn tại"),
    REVIEW_ALREADY_PENDING    ("review_already_pending",    HttpStatus.CONFLICT, "Bình phẩm đã có phiên bản chờ duyệt"),
    INVALID_REVIEW_STATUS     ("invalid_review_status",     HttpStatus.CONFLICT, "Trạng thái bình phẩm không hợp lệ"),

    // ── Bookmark ────────────────────────────────────────────────
    BOOKMARK_NOT_FOUND      ("bookmark_not_found",      HttpStatus.NOT_FOUND, "Bookmark không tồn tại"),
    BOOKMARK_ALREADY_EXISTS ("bookmark_already_exists", HttpStatus.CONFLICT,  "Tác phẩm đã được bookmark"),

    // ── Note ────────────────────────────────────────────────────
    NOTE_NOT_FOUND       ("note_not_found",        HttpStatus.NOT_FOUND, "Ghi chú không tồn tại"),

    // ── Chat ────────────────────────────────────────────────────
    CHAT_SESSION_NOT_FOUND ("chat_session_not_found", HttpStatus.NOT_FOUND, "Phiên chat không tồn tại"),

    // ── Storage ─────────────────────────────────────────────────
    FAIL_TO_UPLOAD            ("fail_to_upload",           HttpStatus.BAD_REQUEST, "Không thể tải lên file"),
    FAIL_TO_GENERATE_PRESIGNED_URL ("fail_to_generate_presigned_url", HttpStatus.BAD_REQUEST, "Không thể tạo presigned URL"),
    FAIL_TO_GENERATE_UPLOAD_SIGNATURE ("fail_to_generate_upload_signature", HttpStatus.BAD_REQUEST, "Không thể tạo chữ ký upload"),
    INVALID_UPLOADED_IMAGE ("invalid_uploaded_image", HttpStatus.BAD_REQUEST, "Ảnh tải lên không hợp lệ");


    private final String code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(String code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
