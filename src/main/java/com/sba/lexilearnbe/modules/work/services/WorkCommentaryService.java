package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkCommentaryResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface WorkCommentaryService {

    Page<WorkCommentaryResponse> getPublishedCommentaries(
            UUID workId,
            int page,
            int size,
            String sortDir,
            String sortBy
    );

    Page<WorkCommentaryResponse> getAllCommentaries(
            UUID workId,
            int page,
            int size,
            String sortDir,
            String sortBy
    );

    WorkCommentaryResponse createCommentary(UUID workId, CreateWorkCommentaryRequest request);

    WorkCommentaryResponse updateCommentary(
            UUID workId,
            UUID commentaryId,
            UpdateWorkCommentaryRequest request
    );

    void deleteCommentary(UUID workId, UUID commentaryId);
}
