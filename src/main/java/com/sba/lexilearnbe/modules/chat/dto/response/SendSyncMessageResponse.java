package com.sba.lexilearnbe.modules.chat.dto.response;

import java.util.List;
import java.util.UUID;

/**
 * Kết quả 1 lượt chat đồng bộ với model đơn. sources chỉ để FE hiển thị tại chỗ — KHÔNG lưu DB
 * (schema chat_messages chỉ có role + content), mở lại chat cũ sẽ không còn sources.
 */
public record SendSyncMessageResponse(
        UUID conversationId,
        String answer,
        String model,
        List<Object> sources
) {
}
