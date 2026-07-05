package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkCommentaryResponse;

import java.util.List;
import java.util.UUID;

public interface WorkCommentaryService {

    List<WorkCommentaryResponse> getPublishedCommentaries(UUID workId);

    List<WorkCommentaryResponse> getAllCommentaries(UUID workId);

    WorkCommentaryResponse createCommentary(UUID workId, CreateWorkCommentaryRequest request);

    WorkCommentaryResponse updateCommentary(
            UUID workId,
            UUID commentaryId,
            UpdateWorkCommentaryRequest request
    );

    void deleteCommentary(UUID workId, UUID commentaryId);
}
