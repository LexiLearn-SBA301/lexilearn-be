package com.sba.lexilearnbe.modules.chat.dto.response;

import java.util.List;
import java.util.UUID;

/** Mở lại 1 đoạn chat cũ: metadata + toàn bộ transcript để render lên màn hình. */
public record ConversationDetailResponse(
        UUID id,
        String title,
        List<ChatMessageResponse> messages
) {
}
