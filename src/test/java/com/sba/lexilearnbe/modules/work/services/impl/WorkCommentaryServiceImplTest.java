package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.entity.WorkCommentary;
import com.sba.lexilearnbe.modules.work.enums.CommentatorType;
import com.sba.lexilearnbe.modules.work.mapper.WorkCommentaryMapper;
import com.sba.lexilearnbe.modules.work.repository.WorkCommentaryRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkCommentaryServiceImplTest {

    private final WorkCommentaryMapper commentaryMapper =
            Mappers.getMapper(WorkCommentaryMapper.class);

    @Test
    void createCommentaryAssignsNextOrderAndDefaults() {
        UUID workId = UUID.randomUUID();
        Work work = Work.builder().id(workId).isPublished(true).build();
        AtomicReference<WorkCommentary> savedCommentary = new AtomicReference<>();

        WorkRepository workRepository = proxy(WorkRepository.class, (method, args) -> {
            if ("findById".equals(method)) {
                return Optional.of(work);
            }
            throw new UnsupportedOperationException(method);
        });
        WorkCommentaryRepository commentaryRepository =
                proxy(WorkCommentaryRepository.class, (method, args) -> {
                    if ("findMaxDisplayOrderByWorkId".equals(method)) {
                        return 2;
                    }
                    if ("saveAndFlush".equals(method)) {
                        WorkCommentary commentary = (WorkCommentary) args[0];
                        savedCommentary.set(commentary);
                        return commentary;
                    }
                    throw new UnsupportedOperationException(method);
                });

        WorkCommentaryServiceImpl service = new WorkCommentaryServiceImpl(
                workRepository,
                commentaryRepository,
                commentaryMapper
        );

        service.createCommentary(
                workId,
                new CreateWorkCommentaryRequest(
                        "  Một cách nhìn  ",
                        "  Nội dung bình phẩm  ",
                        "  Hoài Thanh  ",
                        CommentatorType.CRITIC,
                        "  Thi nhân Việt Nam  ",
                        null,
                        1942,
                        null,
                        null
                )
        );

        WorkCommentary commentary = savedCommentary.get();
        assertEquals(3, commentary.getDisplayOrder());
        assertEquals("Một cách nhìn", commentary.getTitle());
        assertEquals("Nội dung bình phẩm", commentary.getContent());
        assertEquals("Hoài Thanh", commentary.getCommentatorName());
        assertTrue(commentary.getIsPublished());
        assertFalse(commentary.getIsFeatured());
    }

    @Test
    void updateRejectsCommentaryFromAnotherWork() {
        UUID requestedWorkId = UUID.randomUUID();
        UUID actualWorkId = UUID.randomUUID();
        UUID commentaryId = UUID.randomUUID();
        WorkCommentary commentary = WorkCommentary.builder()
                .id(commentaryId)
                .work(Work.builder().id(actualWorkId).build())
                .build();

        WorkCommentaryRepository commentaryRepository =
                proxy(WorkCommentaryRepository.class, (method, args) -> {
                    if ("findByIdWithWork".equals(method)) {
                        return Optional.of(commentary);
                    }
                    throw new UnsupportedOperationException(method);
                });
        WorkRepository workRepository =
                proxy(WorkRepository.class, (method, args) -> {
                    throw new UnsupportedOperationException(method);
                });

        WorkCommentaryServiceImpl service = new WorkCommentaryServiceImpl(
                workRepository,
                commentaryRepository,
                commentaryMapper
        );

        ApiException exception = assertThrows(
                ApiException.class,
                () -> service.updateCommentary(
                        requestedWorkId,
                        commentaryId,
                        new UpdateWorkCommentaryRequest(
                                null, null, null, null, null, null, null, null, null
                        )
                )
        );

        assertEquals(ErrorCode.COMMENTARY_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void mapperUpdatesOnceWhileIgnoringNullValues() {
        WorkCommentary commentary = WorkCommentary.builder()
                .title("Tiêu đề cũ")
                .content("Nội dung cũ")
                .commentatorName("Người cũ")
                .isPublished(true)
                .build();

        commentaryMapper.updateEntityFromRequest(
                new UpdateWorkCommentaryRequest(
                        "   ",
                        null,
                        "  Hoài Thanh  ",
                        null,
                        null,
                        null,
                        null,
                        null,
                        false
                ),
                commentary
        );

        assertNull(commentary.getTitle());
        assertEquals("Nội dung cũ", commentary.getContent());
        assertEquals("Hoài Thanh", commentary.getCommentatorName());
        assertFalse(commentary.getIsPublished());
    }

    @Test
    void publishedCommentariesKeepPageMetadata() {
        UUID workId = UUID.randomUUID();
        Work work = Work.builder().id(workId).isPublished(true).build();
        WorkCommentary commentary = WorkCommentary.builder()
                .id(UUID.randomUUID())
                .work(work)
                .content("Nội dung")
                .commentatorName("Hoài Thanh")
                .commentatorType(CommentatorType.CRITIC)
                .displayOrder(0)
                .isFeatured(false)
                .isPublished(true)
                .build();
        AtomicReference<Pageable> capturedPageable = new AtomicReference<>();

        WorkRepository workRepository = proxy(WorkRepository.class, (method, args) -> {
            if ("findById".equals(method)) {
                return Optional.of(work);
            }
            throw new UnsupportedOperationException(method);
        });
        WorkCommentaryRepository commentaryRepository =
                proxy(WorkCommentaryRepository.class, (method, args) -> {
                    if ("findPublishedByWorkId".equals(method)) {
                        Pageable pageable = (Pageable) args[1];
                        capturedPageable.set(pageable);
                        return new PageImpl<>(List.of(commentary), pageable, 5);
                    }
                    throw new UnsupportedOperationException(method);
                });
        WorkCommentaryServiceImpl service = new WorkCommentaryServiceImpl(
                workRepository,
                commentaryRepository,
                commentaryMapper
        );

        Page<?> result = service.getPublishedCommentaries(
                workId, 1, 2, "desc", "createdAt"
        );

        assertEquals(5, result.getTotalElements());
        assertEquals(3, result.getTotalPages());
        assertEquals(1, result.getNumber());
        assertEquals(1, result.getContent().size());
        assertEquals(2, capturedPageable.get().getPageSize());
        assertEquals(
                Sort.Direction.DESC,
                capturedPageable.get().getSort().getOrderFor("createdAt").getDirection()
        );
    }

    @Test
    void rejectsUnsupportedSortFieldBeforeQueryingDatabase() {
        WorkRepository workRepository =
                proxy(WorkRepository.class, (method, args) -> {
                    throw new UnsupportedOperationException(method);
                });
        WorkCommentaryRepository commentaryRepository =
                proxy(WorkCommentaryRepository.class, (method, args) -> {
                    throw new UnsupportedOperationException(method);
                });
        WorkCommentaryServiceImpl service = new WorkCommentaryServiceImpl(
                workRepository,
                commentaryRepository,
                commentaryMapper
        );

        ApiException exception = assertThrows(
                ApiException.class,
                () -> service.getPublishedCommentaries(
                        UUID.randomUUID(), 0, 10, "asc", "unknownField"
                )
        );

        assertEquals(ErrorCode.VALIDATION_ERROR, exception.getErrorCode());
    }

    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> type, Invocation invocation) {
        return (T) Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class<?>[]{type},
                (proxy, method, args) -> invocation.invoke(method.getName(), args)
        );
    }

    @FunctionalInterface
    private interface Invocation {
        Object invoke(String method, Object[] args);
    }
}
