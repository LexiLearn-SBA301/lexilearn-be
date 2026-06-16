package com.sba.lexilearnbe.modules.workdetail.services;

import com.sba.lexilearnbe.modules.workdetail.dto.request.CreateWorkSectionRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.request.UpdateWorkSectionRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkSectionDetailResponse;
import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkSectionSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface WorkSectionService {

    List<WorkSectionSummaryResponse> getSections(UUID workId);

    WorkSectionDetailResponse getSection(UUID sectionId);

    WorkSectionDetailResponse createSection(
            UUID workId,
            CreateWorkSectionRequest request
    );

    WorkSectionDetailResponse updateSection(
            UUID sectionId,
            UpdateWorkSectionRequest request
    );

    void deleteSection(UUID sectionId);
}