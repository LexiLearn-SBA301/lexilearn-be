package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.response.WorkDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WorkService {
    Page<WorkSummaryResponse> getWorksByFilter(String genre, String period, String search, Pageable pageable);

    WorkDetailResponse getWorkDetail(String slug);
}