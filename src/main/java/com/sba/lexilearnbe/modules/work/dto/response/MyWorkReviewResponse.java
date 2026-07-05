package com.sba.lexilearnbe.modules.work.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record MyWorkReviewResponse(
        UUID reviewId,
        UUID workId,
        String workTitle,
        String workSlug,
        ReviewRevisionResponse approvedRevision,
        ReviewRevisionResponse pendingRevision,
        ReviewRevisionResponse latestRejectedRevision,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
