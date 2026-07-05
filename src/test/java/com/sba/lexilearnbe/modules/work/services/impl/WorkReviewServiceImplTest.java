package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.auth.entity.Account;
import com.sba.lexilearnbe.modules.auth.enums.AccountStatus;
import com.sba.lexilearnbe.modules.auth.repository.AccountRepository;
import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.request.ModerateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.response.AdminWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.dto.response.MyWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.entity.WorkReview;
import com.sba.lexilearnbe.modules.work.entity.WorkReviewRevision;
import com.sba.lexilearnbe.modules.work.enums.ReviewModerationDecision;
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
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorkReviewServiceImplTest {

    private final WorkReviewMapper reviewMapper =
            Mappers.getMapper(WorkReviewMapper.class);

    @Test
    void createReviewStartsWithPendingRevision() {
        ReviewFixtures fixtures = fixtures();
        AtomicReference<WorkReview> savedReview = new AtomicReference<>();

        WorkReviewRepository reviewRepository =
                proxy(WorkReviewRepository.class, (method, args) -> switch (method) {
                    case "existsByAccountIdAndWorkId" -> false;
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
        assertEquals(1, response.pendingRevision().versionNumber());
        assertEquals(
                ReviewRevisionStatus.PENDING,
                response.pendingRevision().status()
        );
        assertEquals("Nội dung", response.pendingRevision().content());
        assertNull(response.approvedRevision());
    }

    @Test
    void createReviewRejectsSecondReviewForSameWork() {
        ReviewFixtures fixtures = fixtures();
        WorkReviewRepository reviewRepository =
                proxy(WorkReviewRepository.class, (method, args) -> {
                    if ("existsByAccountIdAndWorkId".equals(method)) {
                        return true;
                    }
                    throw new UnsupportedOperationException(method);
                });
        WorkReviewRevisionRepository revisionRepository =
                proxy(WorkReviewRevisionRepository.class, (method, args) -> {
                    throw new UnsupportedOperationException(method);
                });
        WorkReviewServiceImpl service = service(
                fixtures, reviewRepository, revisionRepository
        );

        ApiException exception = assertThrows(
                ApiException.class,
                () -> service.createReview(
                        fixtures.account.getId(),
                        fixtures.review.getWork().getId(),
                        new CreateWorkReviewRequest(null, "Nội dung")
                )
        );

        assertEquals(ErrorCode.REVIEW_ALREADY_EXISTS, exception.getErrorCode());
    }

    @Test
    void editingApprovedReviewCreatesPendingAndKeepsApprovedVisible() {
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
                    case "findByReviewIdAndStatus" -> Optional.empty();
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

        assertEquals("Bản đang public", response.approvedRevision().content());
        assertEquals("Bản chỉnh sửa", response.pendingRevision().content());
        assertEquals(2, response.pendingRevision().versionNumber());
        assertEquals(
                ReviewRevisionStatus.PENDING,
                response.pendingRevision().status()
        );
        assertEquals(ReviewRevisionStatus.APPROVED, approved.getStatus());
    }

    @Test
    void editingExistingPendingUpdatesSameRevision() {
        ReviewFixtures fixtures = fixtures();
        WorkReviewRevision approved = revision(
                fixtures.review, 1, "Bản đang public", ReviewRevisionStatus.APPROVED
        );
        WorkReviewRevision pending = revision(
                fixtures.review, 2, "Bản pending cũ", ReviewRevisionStatus.PENDING
        );

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
                    case "findByReviewIdAndStatus" -> Optional.of(pending);
                    case "saveAndFlush" -> args[0];
                    case "findCurrentStatesByReviewIds" -> List.of(approved, pending);
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

        assertEquals(pending.getId(), response.pendingRevision().id());
        assertEquals(2, response.pendingRevision().versionNumber());
        assertEquals("Nội dung mới", response.pendingRevision().content());
        assertEquals("Bản đang public", response.approvedRevision().content());
    }

    @Test
    void approvingPendingRevisionSupersedesCurrentApprovedRevision() {
        ReviewFixtures fixtures = fixtures();
        Account admin = Account.builder()
                .id(UUID.randomUUID())
                .fullName("Admin")
                .email("admin@gmail.com")
                .status(AccountStatus.ACTIVE)
                .build();
        WorkReviewRevision approved = revision(
                fixtures.review, 1, "Bản cũ", ReviewRevisionStatus.APPROVED
        );
        WorkReviewRevision pending = revision(
                fixtures.review, 2, "Bản mới", ReviewRevisionStatus.PENDING
        );

        WorkReviewRepository reviewRepository =
                proxy(WorkReviewRepository.class, (method, args) -> {
                    if ("findByIdForUpdate".equals(method)) {
                        return Optional.of(fixtures.review);
                    }
                    throw new UnsupportedOperationException(method);
                });
        WorkReviewRevisionRepository revisionRepository =
                proxy(WorkReviewRevisionRepository.class, (method, args) -> switch (method) {
                    case "findReviewIdByRevisionId" ->
                            Optional.of(fixtures.review.getId());
                    case "findByIdWithRelations" -> Optional.of(pending);
                    case "findByReviewIdAndStatus" -> Optional.of(approved);
                    case "saveAndFlush" -> args[0];
                    default -> throw new UnsupportedOperationException(method);
                });
        AccountRepository accountRepository =
                proxy(AccountRepository.class, (method, args) -> {
                    if ("findById".equals(method)) {
                        return Optional.of(admin);
                    }
                    throw new UnsupportedOperationException(method);
                });

        WorkReviewServiceImpl service = new WorkReviewServiceImpl(
                fixtures.workRepository,
                accountRepository,
                reviewRepository,
                revisionRepository,
                reviewMapper
        );
        AdminWorkReviewResponse response = service.moderateReview(
                admin.getId(),
                pending.getId(),
                new ModerateWorkReviewRequest(
                        ReviewModerationDecision.APPROVE, null
                )
        );

        assertEquals(ReviewRevisionStatus.SUPERSEDED, approved.getStatus());
        assertEquals(ReviewRevisionStatus.APPROVED, pending.getStatus());
        assertEquals(2, response.approvedRevision().versionNumber());
        assertEquals(admin.getId(), response.revision().reviewedById());
        assertNull(response.revision().rejectionReason());
    }

    @Test
    void rejectingPendingRevisionKeepsCurrentApprovedRevision() {
        ReviewFixtures fixtures = fixtures();
        Account admin = Account.builder()
                .id(UUID.randomUUID())
                .fullName("Admin")
                .email("admin@gmail.com")
                .status(AccountStatus.ACTIVE)
                .build();
        WorkReviewRevision approved = revision(
                fixtures.review, 1, "Bản public", ReviewRevisionStatus.APPROVED
        );
        WorkReviewRevision pending = revision(
                fixtures.review, 2, "Bản bị từ chối", ReviewRevisionStatus.PENDING
        );

        WorkReviewRepository reviewRepository =
                proxy(WorkReviewRepository.class, (method, args) -> {
                    if ("findByIdForUpdate".equals(method)) {
                        return Optional.of(fixtures.review);
                    }
                    throw new UnsupportedOperationException(method);
                });
        WorkReviewRevisionRepository revisionRepository =
                proxy(WorkReviewRevisionRepository.class, (method, args) -> switch (method) {
                    case "findReviewIdByRevisionId" ->
                            Optional.of(fixtures.review.getId());
                    case "findByIdWithRelations" -> Optional.of(pending);
                    case "findByReviewIdAndStatus" -> Optional.of(approved);
                    case "saveAndFlush" -> args[0];
                    default -> throw new UnsupportedOperationException(method);
                });
        AccountRepository accountRepository =
                proxy(AccountRepository.class, (method, args) -> {
                    if ("findById".equals(method)) {
                        return Optional.of(admin);
                    }
                    throw new UnsupportedOperationException(method);
                });
        WorkReviewServiceImpl service = new WorkReviewServiceImpl(
                fixtures.workRepository,
                accountRepository,
                reviewRepository,
                revisionRepository,
                reviewMapper
        );

        AdminWorkReviewResponse response = service.moderateReview(
                admin.getId(),
                pending.getId(),
                new ModerateWorkReviewRequest(
                        ReviewModerationDecision.REJECT,
                        "Nội dung cần chỉnh sửa"
                )
        );

        assertEquals(ReviewRevisionStatus.APPROVED, approved.getStatus());
        assertEquals(ReviewRevisionStatus.REJECTED, pending.getStatus());
        assertEquals("Nội dung cần chỉnh sửa", pending.getRejectionReason());
        assertEquals(1, response.approvedRevision().versionNumber());
    }

    @Test
    void rejectingReviewRequiresReasonBeforeAccessingDatabase() {
        ReviewFixtures fixtures = fixtures();
        WorkReviewRepository reviewRepository =
                proxy(WorkReviewRepository.class, (method, args) -> {
                    throw new UnsupportedOperationException(method);
                });
        WorkReviewRevisionRepository revisionRepository =
                proxy(WorkReviewRevisionRepository.class, (method, args) -> {
                    throw new UnsupportedOperationException(method);
                });
        WorkReviewServiceImpl service = service(
                fixtures, reviewRepository, revisionRepository
        );

        ApiException exception = assertThrows(
                ApiException.class,
                () -> service.moderateReview(
                        UUID.randomUUID(),
                        UUID.randomUUID(),
                        new ModerateWorkReviewRequest(
                                ReviewModerationDecision.REJECT,
                                "   "
                        )
                )
        );

        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
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
