package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkCommentaryResponse;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.entity.WorkCommentary;
import com.sba.lexilearnbe.modules.work.enums.CommentatorType;
import com.sba.lexilearnbe.modules.work.mapper.WorkCommentaryMapper;
import com.sba.lexilearnbe.modules.work.repository.WorkCommentaryRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkCommentaryServiceImplTest {

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
                this::toResponse
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
                this::toResponse
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

    private WorkCommentaryResponse toResponse(WorkCommentary commentary) {
        return new WorkCommentaryResponse(
                commentary.getId(),
                commentary.getWork().getId(),
                commentary.getTitle(),
                commentary.getContent(),
                commentary.getCommentatorName(),
                commentary.getCommentatorType(),
                commentary.getSourceTitle(),
                commentary.getSourceUrl(),
                commentary.getPublishedYear(),
                commentary.getDisplayOrder(),
                commentary.getIsFeatured(),
                commentary.getIsPublished(),
                commentary.getCreatedAt(),
                commentary.getUpdatedAt()
        );
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
