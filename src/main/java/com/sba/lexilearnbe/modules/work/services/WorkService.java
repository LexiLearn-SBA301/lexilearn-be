package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.request.WorkRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface WorkService {
    Page<WorkSummaryResponse> getWorksByFilter(String genre, String period, String search, String tag, Pageable pageable);

    WorkDetailResponse getWorkDetail(String slug);
    WorkDetailResponse createWork(WorkRequest request);

    WorkDetailResponse updateWork(UUID id, WorkRequest request);

    void deleteWork(UUID id);
}