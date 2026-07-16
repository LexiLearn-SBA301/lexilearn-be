package com.sba.lexilearnbe.modules.chat.dto.response;

import com.sba.lexilearnbe.modules.chat.enums.MessageRole;

import java.time.LocalDateTime;

/** 1 lượt chat khi hiển thị lại transcript. */
public record ChatMessageResponse(
        MessageRole role,
        String content,
        LocalDateTime createdAt
) {
}
