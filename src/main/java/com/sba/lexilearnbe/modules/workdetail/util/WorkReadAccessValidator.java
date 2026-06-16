package com.sba.lexilearnbe.modules.workdetail.util;

import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class WorkReadAccessValidator {

    private static final String ADMIN_AUTHORITY = "ROLE_ADMIN";

    private WorkReadAccessValidator() {
    }

    public static void validate(Work work) {
        if (!Boolean.TRUE.equals(work.getIsPublished()) && !isAdmin()) {
            throw new ApiException(ErrorCode.WORK_NOT_FOUND);
        }
    }

    private static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> ADMIN_AUTHORITY.equals(authority.getAuthority()));
    }
}
