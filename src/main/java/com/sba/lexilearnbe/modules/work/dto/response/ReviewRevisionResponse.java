package com.sba.lexilearnbe.modules.work.dto.response;

import com.sba.lexilearnbe.modules.work.enums.ReviewRevisionStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewRevisionResponse(
        UUID id,
        Integer versionNumber,
        String title,
        String content,
        ReviewRevisionStatus status,
        String rejectionReason,
        UUID reviewedById,
        String reviewedByName,
        LocalDateTime reviewedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
