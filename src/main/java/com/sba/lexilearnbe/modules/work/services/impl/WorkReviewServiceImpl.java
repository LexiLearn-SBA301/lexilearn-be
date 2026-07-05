package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.auth.entity.Account;
import com.sba.lexilearnbe.modules.auth.enums.AccountStatus;
import com.sba.lexilearnbe.modules.auth.repository.AccountRepository;
import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.request.ModerateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.response.AdminWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.dto.response.MyWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.dto.response.PublicWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.dto.response.ReviewRevisionResponse;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.entity.WorkReview;
import com.sba.lexilearnbe.modules.work.entity.WorkReviewRevision;
import com.sba.lexilearnbe.modules.work.enums.ReviewModerationDecision;
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
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkReviewServiceImpl implements WorkReviewService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Collection<ReviewRevisionStatus> CURRENT_STATE_STATUSES =
            List.of(ReviewRevisionStatus.APPROVED, ReviewRevisionStatus.PENDING);
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
    private static final Map<String, String> ADMIN_SORT_FIELDS = Map.of(
            "createdAt", "createdAt",
            "reviewedAt", "reviewedAt",
            "versionNumber", "versionNumber",
            "reviewerName", "review.account.fullName",
            "workTitle", "review.work.title"
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
        if (reviewRepository.existsByAccountIdAndWorkId(accountId, workId)) {
            throw new ApiException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        WorkReview review = reviewMapper.toEntity(work, account);
        try {
            review = reviewRepository.saveAndFlush(review);
        }
        catch (DataIntegrityViolationException exception) {
            throw new ApiException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        WorkReviewRevision revision = reviewMapper.toRevisionEntity(request);
        revision.setReview(review);
        revision.setVersionNumber(1);
        revision.setStatus(ReviewRevisionStatus.PENDING);
        revision = revisionRepository.saveAndFlush(revision);

        return reviewMapper.toMyResponse(review, null, reviewMapper.toRevisionResponse(revision), null);
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

        WorkReviewRevision pending = revisionRepository.findByReviewIdAndStatus(
                        reviewId, ReviewRevisionStatus.PENDING
                )
                .orElse(null);

        if (pending == null) {
            WorkReviewRevision latest = revisionRepository.findLatestByReviewId(reviewId)
                    .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_REVISION_NOT_FOUND));
            pending = reviewMapper.toPendingRevision(latest);
            pending.setReview(review);
            pending.setVersionNumber(
                    revisionRepository.findMaxVersionByReviewId(reviewId) + 1
            );
            pending.setStatus(ReviewRevisionStatus.PENDING);
        }

        reviewMapper.updateEntityFromRequest(request, pending);
        pending.setRejectionReason(null);
        pending.setReviewedBy(null);
        pending.setReviewedAt(null);

        try {
            pending = revisionRepository.saveAndFlush(pending);
        }
        catch (DataIntegrityViolationException exception) {
            throw new ApiException(ErrorCode.REVIEW_ALREADY_PENDING, "Bình phẩm đã có một phiên bản đang chờ duyệt"
            );
        }

        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);
        ReviewState state = loadState(review);
        state.put(pending);
        return toMyResponse(review, state);
    }

    @Override
    @Transactional
    public void deleteMyReview(UUID accountId, UUID reviewId) {
        WorkReview review = reviewRepository.findOwnedByIdForUpdate(reviewId, accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_FOUND));
        reviewRepository.delete(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminWorkReviewResponse> getReviewsForModeration(String status, int page, int size, String sortDir, String sortBy) {

        ReviewRevisionStatus parsedStatus = parseStatus(status);

        Pageable pageable = createPageable(page, size, sortDir, sortBy, ADMIN_SORT_FIELDS);

        Page<WorkReviewRevision> revisions = revisionRepository.findAllByStatus(parsedStatus, pageable);

        Map<UUID, WorkReviewRevision> approvedByReview = loadApprovedByReview(revisions.getContent());

        return revisions.map(
                revision -> reviewMapper.toAdminResponse( revision,
                                                                            toRevisionResponse(approvedByReview.get(revision.getReview().getId()))));
    }

    @Override
    @Transactional(readOnly = true)
    public AdminWorkReviewResponse getModerationDetail(UUID revisionId) {
        WorkReviewRevision revision = requireRevision(revisionId);
        WorkReviewRevision approved = revisionRepository.findByReviewIdAndStatus(revision.getReview().getId(), ReviewRevisionStatus.APPROVED).orElse(null);
        return reviewMapper.toAdminResponse(revision, toRevisionResponse(approved)
        );
    }

    @Override
    @Transactional
    public AdminWorkReviewResponse moderateReview(UUID adminId, UUID revisionId, ModerateWorkReviewRequest request) {

        validateModerationRequest(request);

        Account admin = requireAccount(adminId);

        UUID reviewId = revisionRepository.findReviewIdByRevisionId(revisionId)
                .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_REVISION_NOT_FOUND));

        reviewRepository.findByIdForUpdate(reviewId)
                .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_NOT_FOUND));

        WorkReviewRevision revision = requireRevision(revisionId);
        if (revision.getStatus() != ReviewRevisionStatus.PENDING) {
            throw new ApiException(ErrorCode.INVALID_REVIEW_STATUS, "Chỉ có thể kiểm duyệt phiên bản đang chờ duyệt");
        }

        LocalDateTime reviewedAt = LocalDateTime.now();
        if (request.decision() == ReviewModerationDecision.APPROVE) {
            revisionRepository.findByReviewIdAndStatus(reviewId, ReviewRevisionStatus.APPROVED)
                    .ifPresent(current -> {current.setStatus(ReviewRevisionStatus.SUPERSEDED);
                        revisionRepository.saveAndFlush(current);
                    });
            revision.setStatus(ReviewRevisionStatus.APPROVED);
            revision.setRejectionReason(null);
        }
        else {
            revision.setStatus(ReviewRevisionStatus.REJECTED);
            revision.setRejectionReason(request.rejectionReason().trim());
        }
        revision.setReviewedBy(admin);
        revision.setReviewedAt(reviewedAt);
        revision = revisionRepository.saveAndFlush(revision);

        WorkReviewRevision approved = revision.getStatus() == ReviewRevisionStatus.APPROVED ? revision : revisionRepository.findByReviewIdAndStatus(reviewId, ReviewRevisionStatus.APPROVED)
                .orElse(null);
        return reviewMapper.toAdminResponse(revision, toRevisionResponse(approved));
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

    private WorkReviewRevision requireRevision(UUID revisionId) {

        if (revisionId == null){
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "revisionId không được để trống");
        }
        return revisionRepository.findByIdWithRelations(revisionId)
                .orElseThrow(() -> new ApiException(ErrorCode.REVIEW_REVISION_NOT_FOUND));
    }

    private void validateUpdateRequest(UpdateWorkReviewRequest request) {
        if (request.title() == null && request.content() == null) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Phải gửi ít nhất một nội dung cần cập nhật");
        }
    }

    private void validateModerationRequest(ModerateWorkReviewRequest request) {
        boolean hasReason = StringUtils.hasText(request.rejectionReason());
        if (request.decision() == ReviewModerationDecision.REJECT && !hasReason) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Phải nhập lý do khi từ chối bình phẩm");
        }
    }

    private ReviewRevisionStatus parseStatus(String status) {
        if (!StringUtils.hasText(status)) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Trạng thái phiên bản không được để trống");
        }
        try {
            return ReviewRevisionStatus.valueOf(status.trim().toUpperCase());
        }
        catch (IllegalArgumentException exception) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Trạng thái phiên bản không hợp lệ");
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

    private Map<UUID, WorkReviewRevision> loadApprovedByReview(
            List<WorkReviewRevision> revisions) {
        List<UUID> reviewIds = revisions.stream()
                .map(revision -> revision.getReview().getId())
                .distinct()
                .toList();
        if (reviewIds.isEmpty()) {
            return Map.of();
        }
        return revisionRepository.findCurrentStatesByReviewIds(
                        reviewIds,
                        List.of(ReviewRevisionStatus.APPROVED),
                        ReviewRevisionStatus.REJECTED
                )
                .stream()
                .filter(revision -> revision.getStatus() == ReviewRevisionStatus.APPROVED)
                .collect(Collectors.toMap(
                        revision -> revision.getReview().getId(),
                        Function.identity()
                ));
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
