package com.sba.lexilearnbe.modules.work.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record PublicWorkReviewResponse(
        UUID reviewId,
        UUID workId,
        UUID reviewerId,
        String reviewerName,
        String title,
        String content,
        Integer versionNumber,
        LocalDateTime approvedAt,
        LocalDateTime createdAt
) {
}
