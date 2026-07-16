package com.sba.lexilearnbe.modules.chat.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/** 1 dòng trong danh sách hội thoại (sidebar). */
public record ConversationSummaryResponse(
        UUID id,
        String title,
        LocalDateTime updatedAt
) {
}
