package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.auth.entity.Account;
import com.sba.lexilearnbe.modules.auth.enums.AccountStatus;
import com.sba.lexilearnbe.modules.auth.repository.AccountRepository;
import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.response.MyWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.entity.WorkReview;
import com.sba.lexilearnbe.modules.work.entity.WorkReviewRevision;
import com.sba.lexilearnbe.modules.work.enums.ReviewRevisionStatus;
import com.sba.lexilearnbe.modules.work.mapper.WorkReviewMapper;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkReviewRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkReviewRevisionRepository;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WorkReviewServiceImplTest {

    private final WorkReviewMapper reviewMapper =
            Mappers.getMapper(WorkReviewMapper.class);

    @Test
    void createReviewStartsWithApprovedRevision() {
        ReviewFixtures fixtures = fixtures();
        AtomicReference<WorkReview> savedReview = new AtomicReference<>();

        WorkReviewRepository reviewRepository =
                proxy(WorkReviewRepository.class, (method, args) -> switch (method) {
                    case "saveAndFlush" -> {
                        WorkReview review = (WorkReview) args[0];
                        review.setId(fixtures.review.getId());
                        savedReview.set(review);
                        yield review;
                    }
                    default -> throw new UnsupportedOperationException(method);
                });
        WorkReviewRevisionRepository revisionRepository =
                proxy(WorkReviewRevisionRepository.class, (method, args) -> {
                    if ("saveAndFlush".equals(method)) {
                        return args[0];
                    }
                    throw new UnsupportedOperationException(method);
                });
        WorkReviewServiceImpl service = service(
                fixtures, reviewRepository, revisionRepository
        );

        MyWorkReviewResponse response = service.createReview(
                fixtures.account.getId(),
                fixtures.review.getWork().getId(),
                new CreateWorkReviewRequest(" Cảm nhận ", " Nội dung ")
        );

        assertEquals(fixtures.review.getId(), savedReview.get().getId());
        assertEquals(1, response.approvedRevision().versionNumber());
        assertEquals(
                ReviewRevisionStatus.APPROVED,
                response.approvedRevision().status()
        );
        assertEquals("Nội dung", response.approvedRevision().content());
        assertNull(response.pendingRevision());
    }

    @Test
    void createReviewAllowsMultipleReviewsForSameWork() {
        ReviewFixtures fixtures = fixtures();
        AtomicInteger savedReviews = new AtomicInteger();
        WorkReviewRepository reviewRepository =
                proxy(WorkReviewRepository.class, (method, args) -> switch (method) {
                    case "saveAndFlush" -> {
                        WorkReview review = (WorkReview) args[0];
                        review.setId(UUID.randomUUID());
                        savedReviews.incrementAndGet();
                        yield review;
                    }
                    default -> throw new UnsupportedOperationException(method);
                });
        WorkReviewRevisionRepository revisionRepository =
                proxy(WorkReviewRevisionRepository.class, (method, args) -> {
                    if ("saveAndFlush".equals(method)) {
                        return args[0];
                    }
                    throw new UnsupportedOperationException(method);
                });
        WorkReviewServiceImpl service = service(
                fixtures, reviewRepository, revisionRepository
        );

        service.createReview(
                fixtures.account.getId(),
                fixtures.review.getWork().getId(),
                new CreateWorkReviewRequest(null, "Nội dung 1")
        );
        MyWorkReviewResponse second = service.createReview(
                fixtures.account.getId(),
                fixtures.review.getWork().getId(),
                new CreateWorkReviewRequest(null, "Nội dung 2")
        );

        assertEquals(2, savedReviews.get());
        assertEquals("Nội dung 2", second.approvedRevision().content());
        assertEquals(ReviewRevisionStatus.APPROVED, second.approvedRevision().status());
    }

    @Test
    void editingApprovedReviewPublishesNewApprovedRevisionImmediately() {
        ReviewFixtures fixtures = fixtures();
        WorkReviewRevision approved = revision(
                fixtures.review, 1, "Bản đang public", ReviewRevisionStatus.APPROVED
        );
        AtomicReference<WorkReviewRevision> saved = new AtomicReference<>();

        WorkReviewRepository reviewRepository =
                proxy(WorkReviewRepository.class, (method, args) -> {
                    if ("findOwnedByIdForUpdate".equals(method)) {
                        return Optional.of(fixtures.review);
                    }
                    if ("save".equals(method)) {
                        return args[0];
                    }
                    throw new UnsupportedOperationException(method);
                });
        WorkReviewRevisionRepository revisionRepository =
                proxy(WorkReviewRevisionRepository.class, (method, args) -> switch (method) {
                    case "findByReviewIdAndStatus" -> Optional.of(approved);
                    case "findLatestByReviewId" -> Optional.of(approved);
                    case "findMaxVersionByReviewId" -> 1;
                    case "saveAndFlush" -> {
                        WorkReviewRevision revision = (WorkReviewRevision) args[0];
                        saved.set(revision);
                        yield revision;
                    }
                    case "findCurrentStatesByReviewIds" ->
                            List.of(approved, saved.get());
                    default -> throw new UnsupportedOperationException(method);
                });

        WorkReviewServiceImpl service = service(
                fixtures, reviewRepository, revisionRepository
        );
        MyWorkReviewResponse response = service.updateMyReview(
                fixtures.account.getId(),
                fixtures.review.getId(),
                new UpdateWorkReviewRequest(null, "Bản chỉnh sửa")
        );

        assertEquals("Bản chỉnh sửa", response.approvedRevision().content());
        assertNull(response.pendingRevision());
        assertEquals(2, response.approvedRevision().versionNumber());
        assertEquals(
                ReviewRevisionStatus.APPROVED,
                response.approvedRevision().status()
        );
        assertEquals(ReviewRevisionStatus.SUPERSEDED, approved.getStatus());
    }

    @Test
    void editingReviewWithoutCurrentApprovedStillPublishesImmediately() {
        ReviewFixtures fixtures = fixtures();
        WorkReviewRevision latest = revision(
                fixtures.review, 1, "Bản cũ", ReviewRevisionStatus.REJECTED
        );
        AtomicReference<WorkReviewRevision> saved = new AtomicReference<>();

        WorkReviewRepository reviewRepository =
                proxy(WorkReviewRepository.class, (method, args) -> {
                    if ("findOwnedByIdForUpdate".equals(method)) {
                        return Optional.of(fixtures.review);
                    }
                    if ("save".equals(method)) {
                        return args[0];
                    }
                    throw new UnsupportedOperationException(method);
                });
        WorkReviewRevisionRepository revisionRepository =
                proxy(WorkReviewRevisionRepository.class, (method, args) -> switch (method) {
                    case "findByReviewIdAndStatus" -> Optional.empty();
                    case "findLatestByReviewId" -> Optional.of(latest);
                    case "findMaxVersionByReviewId" -> 1;
                    case "saveAndFlush" -> {
                        WorkReviewRevision revision = (WorkReviewRevision) args[0];
                        saved.set(revision);
                        yield revision;
                    }
                    case "findCurrentStatesByReviewIds" -> List.of(saved.get());
                    default -> throw new UnsupportedOperationException(method);
                });

        WorkReviewServiceImpl service = service(
                fixtures, reviewRepository, revisionRepository
        );
        MyWorkReviewResponse response = service.updateMyReview(
                fixtures.account.getId(),
                fixtures.review.getId(),
                new UpdateWorkReviewRequest("Tiêu đề mới", "Nội dung mới")
        );

        assertEquals(2, response.approvedRevision().versionNumber());
        assertEquals("Nội dung mới", response.approvedRevision().content());
        assertEquals(ReviewRevisionStatus.APPROVED, response.approvedRevision().status());
        assertNull(response.pendingRevision());
    }

    private WorkReviewServiceImpl service(
            ReviewFixtures fixtures,
            WorkReviewRepository reviewRepository,
            WorkReviewRevisionRepository revisionRepository) {
        return new WorkReviewServiceImpl(
                fixtures.workRepository,
                fixtures.accountRepository,
                reviewRepository,
                revisionRepository,
                reviewMapper
        );
    }

    private ReviewFixtures fixtures() {
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .fullName("Độc giả")
                .email("reader@example.com")
                .status(AccountStatus.ACTIVE)
                .build();
        Work work = Work.builder()
                .id(UUID.randomUUID())
                .title("Tác phẩm")
                .slug("tac-pham")
                .isPublished(true)
                .build();
        WorkReview review = WorkReview.builder()
                .id(UUID.randomUUID())
                .work(work)
                .account(account)
                .build();
        WorkRepository workRepository =
                proxy(WorkRepository.class, (method, args) -> {
                    if ("findById".equals(method)) {
                        return Optional.of(work);
                    }
                    throw new UnsupportedOperationException(method);
                });
        AccountRepository accountRepository =
                proxy(AccountRepository.class, (method, args) -> {
                    if ("findById".equals(method)) {
                        return Optional.of(account);
                    }
                    throw new UnsupportedOperationException(method);
                });
        return new ReviewFixtures(
                account, workRepository, accountRepository, review
        );
    }

    private WorkReviewRevision revision(
            WorkReview review,
            int version,
            String content,
            ReviewRevisionStatus status) {
        return WorkReviewRevision.builder()
                .id(UUID.randomUUID())
                .review(review)
                .versionNumber(version)
                .content(content)
                .status(status)
                .build();
    }

    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> type, Invocation invocation) {
        return (T) Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[]{type},
                (proxy, method, args) -> invocation.invoke(method.getName(), args)
        );
    }

    private record ReviewFixtures(
            Account account,
            WorkRepository workRepository,
            AccountRepository accountRepository,
            WorkReview review
    ) {
    }

    @FunctionalInterface
    private interface Invocation {
        Object invoke(String method, Object[] args);
    }
}
