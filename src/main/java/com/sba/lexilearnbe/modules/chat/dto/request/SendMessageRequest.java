package com.sba.lexilearnbe.modules.chat.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Gửi 1 tin nhắn. conversationId = null -> tạo đoạn mới (lazy-create ở tin nhắn đầu);
 * có giá trị -> chat tiếp đoạn đó. Đáp trả bằng SSE, event đầu tiên báo conversationId thực.
 */
public record SendMessageRequest(
        UUID conversationId,

        @NotBlank(message = "Tin nhắn không được để trống")
        @Size(max = 2000, message = "Tin nhắn tối đa 2000 ký tự")
        String message
) {
}
