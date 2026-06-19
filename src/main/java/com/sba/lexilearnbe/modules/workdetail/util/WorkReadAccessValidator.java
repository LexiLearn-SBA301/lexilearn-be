package com.sba.lexilearnbe.modules.workdetail.util;

import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;

public final class WorkReadAccessValidator {

    private WorkReadAccessValidator() {
    }

    public static void validate(Work work) {
        if (!Boolean.TRUE.equals(work.getIsPublished())) {
            throw new ApiException(ErrorCode.WORK_NOT_FOUND);
        }
    }

    public static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, fieldName + " không được để trống");
        }
    }
}
