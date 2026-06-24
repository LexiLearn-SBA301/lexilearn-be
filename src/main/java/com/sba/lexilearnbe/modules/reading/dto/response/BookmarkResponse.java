package com.sba.lexilearnbe.modules.reading.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookmarkResponse(
        UUID id,
        BookmarkWorkResponse work,
        BookmarkSectionResponse currentSection,
        Integer position,
        BigDecimal progressPercent,
        Boolean isCompleted,
        LocalDateTime completedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
