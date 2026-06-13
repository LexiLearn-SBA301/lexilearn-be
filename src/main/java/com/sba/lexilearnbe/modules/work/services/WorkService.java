package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.response.WorkDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSummaryResponse;
import org.springframework.data.domain.Page;

public interface WorkService {
    Page<WorkSummaryResponse> getWorksByFilter(String genre, String period, String searchKeyword, int page, int size, String sort);

    WorkDetailResponse getWorkDetail(String slug);
}