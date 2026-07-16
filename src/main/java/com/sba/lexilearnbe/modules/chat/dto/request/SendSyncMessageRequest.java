package com.sba.lexilearnbe.modules.chat.dto.request;

import com.sba.lexilearnbe.modules.chat.enums.ChatModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Gửi 1 tin nhắn tới model đơn (only-llm / base-llm) — trả JSON đồng bộ, KHÔNG stream, KHÔNG nhớ
 * ngữ cảnh. Chỉ lưu transcript để xem lại. conversationId = null -> tạo đoạn mới (lazy-create).
 */
public record SendSyncMessageRequest(
        UUID conversationId,

        @NotBlank(message = "Tin nhắn không được để trống")
        @Size(max = 2000, message = "Tin nhắn tối đa 2000 ký tự")
        String message,

        @NotNull(message = "Thiếu model")
        ChatModel model
) {
}
