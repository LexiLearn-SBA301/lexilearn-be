package com.sba.lexilearnbe.modules.work.dto.response;

import java.util.UUID;

public record AdminWorkReviewResponse(
        UUID reviewId,
        UUID workId,
        String workTitle,
        String workSlug,
        UUID reviewerId,
        String reviewerName,
        String reviewerEmail,
        ReviewRevisionResponse revision,
        ReviewRevisionResponse approvedRevision
) {
}
