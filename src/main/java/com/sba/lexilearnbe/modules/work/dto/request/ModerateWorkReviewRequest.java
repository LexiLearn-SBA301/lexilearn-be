package com.sba.lexilearnbe.modules.work.dto.request;

import com.sba.lexilearnbe.modules.work.enums.ReviewModerationDecision;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ModerateWorkReviewRequest(
        @NotNull(message = "Quyết định kiểm duyệt không được để trống")
        ReviewModerationDecision decision,

        @Size(max = 1000, message = "Lý do từ chối tối đa 1000 ký tự")
        String rejectionReason
) {
}
