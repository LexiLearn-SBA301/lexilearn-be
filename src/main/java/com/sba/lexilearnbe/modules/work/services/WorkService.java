package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.request.WorkRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface WorkService {
    Page<WorkSummaryResponse> getWorksByFilter(String genre, String subGenre, String period, String search, String tag, Pageable pageable);

    List<String> getGenres();

    List<String> getSubGenres(String genre);

    WorkDetailResponse getWorkDetail(String slug);
    WorkDetailResponse createWork(WorkRequest request);

    WorkDetailResponse updateWork(UUID id, WorkRequest request);

    WorkDetailResponse deleteCover(UUID id);
    void deleteWork(UUID id);
}
