package com.sba.lexilearnbe.modules.chat.enums;

/**
 * Vai trò của 1 lượt chat. Lưu DB dạng USER/ASSISTANT (hoa, theo convention enum + CHECK
 * constraint). Khi gửi lịch sử cho AI service thì đổi sang chữ thường ("user"/"assistant").
 */
public enum MessageRole {
    USER,
    ASSISTANT;

    /** Định dạng role mà AI service (rag-service) mong đợi trong payload history. */
    public String toAiRole() {
        return name().toLowerCase();
    }
}
