package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkSectionRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkSectionRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSectionDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSectionSummaryResponse;

import java.util.List;
import java.util.UUID;

public interface WorkSectionService {

    List<WorkSectionSummaryResponse> getSections(UUID workId);

    WorkSectionDetailResponse getSection(UUID workId, UUID sectionId);

    WorkSectionDetailResponse createSection(UUID workId, CreateWorkSectionRequest request);

    WorkSectionDetailResponse updateSection(UUID sectionId, UpdateWorkSectionRequest request);

    void deleteSection(UUID sectionId);
}
