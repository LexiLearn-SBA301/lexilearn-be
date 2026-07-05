package com.sba.lexilearnbe.modules.work.dto.response;

import com.sba.lexilearnbe.modules.work.enums.CommentatorType;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkCommentaryResponse(
        UUID id,
        UUID workId,
        String title,
        String content,
        String commentatorName,
        CommentatorType commentatorType,
        String sourceTitle,
        String sourceUrl,
        Integer publishedYear,
        Integer displayOrder,
        Boolean isFeatured,
        Boolean isPublished,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
