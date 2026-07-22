package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.response.MyWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.dto.response.PublicWorkReviewResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface WorkReviewService {

    Page<PublicWorkReviewResponse> getPublishedReviews(UUID workId, int page, int size, String sortDir, String sortBy);

    MyWorkReviewResponse createReview(UUID accountId, UUID workId, CreateWorkReviewRequest request);

    Page<MyWorkReviewResponse> getMyReviews(UUID accountId, int page, int size, String sortDir, String sortBy);

    MyWorkReviewResponse getMyReview(UUID accountId, UUID reviewId);

    MyWorkReviewResponse updateMyReview(UUID accountId, UUID reviewId, UpdateWorkReviewRequest request);

    void deleteMyReview(UUID accountId, UUID reviewId);
}
