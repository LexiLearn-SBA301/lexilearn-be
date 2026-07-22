package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.auth.entity.Account;
import com.sba.lexilearnbe.modules.auth.enums.AccountStatus;
import com.sba.lexilearnbe.modules.auth.repository.AccountRepository;
import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.response.MyWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.dto.response.PublicWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.dto.response.ReviewRevisionResponse;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.entity.WorkReview;
import com.sba.lexilearnbe.modules.work.entity.WorkReviewRevision;
import com.sba.lexilearnbe.modules.work.enums.ReviewRevisionStatus;
import com.sba.lexilearnbe.modules.work.mapper.WorkReviewMapper;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkReviewRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkReviewRevisionRepository;
import com.sba.lexilearnbe.modules.work.services.WorkReviewService;
import com.sba.lexilearnbe.modules.work.utils.WorkReadAccessValidator;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkReviewServiceImpl implements WorkReviewService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Collection<ReviewRevisionStatus> CURRENT_STATE_STATUSES =
            List.of(ReviewRevisionStatus.APPROVED);
    private static final Map<String, String> PUBLIC_SORT_FIELDS = Map.of(
            "createdAt", "createdAt",
            "reviewedAt", "reviewedAt",
            "versionNumber", "versionNumber"
    );
    private static final Map<String, String> MY_SORT_FIELDS = Map.of(
            "createdAt", "createdAt",
            "updatedAt", "updatedAt",
            "workTitle", "work.title"
    );

    private final WorkRepository workRepository;
    private final AccountRepository accountRepository;
    private final WorkReviewRepository reviewRepository;
    private final WorkReviewRevisionRepository revisionRepository;
    private final WorkReviewMapper reviewMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<PublicWorkReviewResponse> getPublishedReviews(UUID workId, int page, int size, String sortDir, String sortBy) {

        requireReadableWork(workId);

        Pageable pageable = createPageable(page, size, sortDir, sortBy, PUBLIC_SORT_FIELDS);

        return revisionRepository.findPublicByWorkId(workId, ReviewRevisionStatus.APPROVED, pageable).map(reviewMapper::toPublicResponse);
    }

    @Override
    @Transactional
    public MyWorkReviewResponse createReview(UUID accountId, UUID workId, CreateWorkReviewRequest request) {

        Account account = requireAccount(accountId);
        Work work = requireReadableWork(workId);

        WorkReview review = reviewMapper.toEntity(work, account);
        review = reviewRepository.saveAndFlush(review);

        WorkReviewRevision revision = reviewMapper.toRevisionEntity(request);
        revision.setReview(review);
        revision.setVersionNumber(1);
        revision.setStatus(ReviewRevisionStatus.APPROVED);
        revision.setReviewedAt(LocalDateTime.now());
        revision = revisionRepository.saveAndFlush(revision);

        return reviewMapper.toMyResponse(review, reviewMapper.toRevisionResponse(revision), null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MyWorkReviewResponse> getMyReviews(UUID accountId, int page, int size, String sortDir, String sortBy) {

        requireAccount(accountId);
        Pageable pageable = createPageable(page, size, sortDir, sortBy, MY_SORT_FIELDS);

        Page<WorkReview> reviews = reviewRepository.findAllByAccountId(accountId, pageable);

        Map<UUID, ReviewState> states = loadStates(reviews.getContent());

        return reviews.map(review -> toMyResponse(review, states.get(review.getId())));
    }

    @Override
    @Transactional(readOnly = true)
    public MyWorkReviewResponse getMyReview(UUID accountId, UUID reviewId) {

        requireAccount(accountId);

        WorkReview review = reviewRepository.findOwnedById(reviewId, accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_FOUND));

        return toMyResponse(review, loadState(review));
    }

    @Override
    @Transactional
    public MyWorkReviewResponse updateMyReview(UUID accountId, UUID reviewId, UpdateWorkReviewRequest request) {

        validateUpdateRequest(request);
        WorkReview review = reviewRepository.findOwnedByIdForUpdate(reviewId, accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_FOUND));

        WorkReviewRevision currentApproved = revisionRepository.findByReviewIdAndStatus(
                        reviewId, ReviewRevisionStatus.APPROVED
                )
                .orElse(null);
        if (currentApproved != null) {
            currentApproved.setStatus(ReviewRevisionStatus.SUPERSEDED);
            revisionRepository.saveAndFlush(currentApproved);
        }

        WorkReviewRevision latest = revisionRepository.findLatestByReviewId(reviewId)
                .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_REVISION_NOT_FOUND));
        WorkReviewRevision nextApproved = reviewMapper.toPendingRevision(latest);
        nextApproved.setReview(review);
        nextApproved.setVersionNumber(
                revisionRepository.findMaxVersionByReviewId(reviewId) + 1
        );
        nextApproved.setStatus(ReviewRevisionStatus.APPROVED);
        nextApproved.setRejectionReason(null);
        nextApproved.setReviewedBy(null);
        nextApproved.setReviewedAt(LocalDateTime.now());
        reviewMapper.updateEntityFromRequest(request, nextApproved);
        nextApproved = revisionRepository.saveAndFlush(nextApproved);

        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);
        ReviewState state = loadState(review);
        state.put(nextApproved);
        return toMyResponse(review, state);
    }

    @Override
    @Transactional
    public void deleteMyReview(UUID accountId, UUID reviewId) {
        WorkReview review = reviewRepository.findOwnedByIdForUpdate(reviewId, accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_FOUND));
        reviewRepository.delete(review);
    }

    private Account requireAccount(UUID accountId) {

        if (accountId == null){
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "accountId không được để trống");
        }

        Account account = accountRepository.findById(accountId).orElseThrow(() -> new ApiException(ErrorCode.ACCOUNT_NOT_FOUND));
        if (account.getStatus() == AccountStatus.LOCKED) {
            throw new ApiException(ErrorCode.ACCOUNT_LOCKED);
        }
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new ApiException(ErrorCode.ACCOUNT_NOT_VERIFIED);
        }
        return account;
    }

    private Work requireReadableWork(UUID workId) {
        if (workId == null){
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "workId không được để trống");
        }
        Work work = workRepository.findById(workId)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));
        WorkReadAccessValidator.validate(work);
        return work;
    }

    private void validateUpdateRequest(UpdateWorkReviewRequest request) {
        if (request.title() == null && request.content() == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Phải gửi ít nhất một nội dung cần cập nhật");
        }
    }

    private Map<UUID, ReviewState> loadStates(List<WorkReview> reviews) {
        if (reviews.isEmpty()) {
            return Map.of();
        }
        List<UUID> reviewIds = reviews.stream().map(WorkReview::getId).toList();
        Map<UUID, ReviewState> states = new HashMap<>();
        revisionRepository.findCurrentStatesByReviewIds(reviewIds, CURRENT_STATE_STATUSES, ReviewRevisionStatus.REJECTED)
                .forEach(revision -> states
                        .computeIfAbsent(
                                revision.getReview().getId(),
                                ignored -> new ReviewState()
                        )
                        .put(revision));
        return states;
    }

    private ReviewState loadState(WorkReview review) {
        return loadStates(List.of(review)).getOrDefault(review.getId(), new ReviewState());
    }

    private MyWorkReviewResponse toMyResponse(WorkReview review, ReviewState state) {

        ReviewState safeState = state == null ? new ReviewState() : state;
        return reviewMapper.toMyResponse(review,
                                        toRevisionResponse(safeState.get(ReviewRevisionStatus.APPROVED)),
                                        toRevisionResponse(safeState.get(ReviewRevisionStatus.PENDING)),
                                        toRevisionResponse(safeState.get(ReviewRevisionStatus.REJECTED))
        );
    }

    private ReviewRevisionResponse toRevisionResponse(WorkReviewRevision revision) {
        return revision == null ? null : reviewMapper.toRevisionResponse(revision);
    }

    private Pageable createPageable(int page, int size, String sortDir, String sortBy, Map<String, String> allowedSortFields) {

        if (page < 0) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Số trang không được nhỏ hơn 0");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Kích thước trang phải từ 1 đến 100");
        }
        String property = allowedSortFields.get(sortBy);
        if (property == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Trường sắp xếp không hợp lệ");
        }

        Sort.Direction direction;
        if ("asc".equalsIgnoreCase(sortDir)) {
            direction = Sort.Direction.ASC;
        }
        else if ("desc".equalsIgnoreCase(sortDir)) {
            direction = Sort.Direction.DESC;
        }
        else {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Chiều sắp xếp chỉ nhận asc hoặc desc");
        }
        return PageRequest.of(page, size, Sort.by(direction, property));
    }

    private static class ReviewState {
        private final Map<ReviewRevisionStatus, WorkReviewRevision> revisions =
                new EnumMap<>(ReviewRevisionStatus.class);

        void put(WorkReviewRevision revision) {
            revisions.put(revision.getStatus(), revision);
        }

        WorkReviewRevision get(ReviewRevisionStatus status) {
            return revisions.get(status);
        }
    }
}
