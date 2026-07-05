package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkCommentaryResponse;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.entity.WorkCommentary;
import com.sba.lexilearnbe.modules.work.mapper.WorkCommentaryMapper;
import com.sba.lexilearnbe.modules.work.repository.WorkCommentaryRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.work.services.WorkCommentaryService;
import com.sba.lexilearnbe.modules.work.utils.WorkReadAccessValidator;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkCommentaryServiceImpl implements WorkCommentaryService {

    private final WorkRepository workRepository;
    private final WorkCommentaryRepository commentaryRepository;
    private final WorkCommentaryMapper commentaryMapper;

    @Override
    @Transactional(readOnly = true)
    public List<WorkCommentaryResponse> getPublishedCommentaries(UUID workId) {
        requireReadableWork(workId);
        return commentaryRepository.findPublishedByWorkId(workId)
                .stream()
                .map(commentaryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkCommentaryResponse> getAllCommentaries(UUID workId) {
        requireWork(workId);
        return commentaryRepository.findAllByWorkId(workId)
                .stream()
                .map(commentaryMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public WorkCommentaryResponse createCommentary(
            UUID workId,
            CreateWorkCommentaryRequest request) {
        Work work = requireWork(workId);
        WorkCommentary commentary = WorkCommentary.builder()
                .work(work)
                .title(trimToNull(request.title()))
                .content(request.content().trim())
                .commentatorName(request.commentatorName().trim())
                .commentatorType(request.commentatorType())
                .sourceTitle(trimToNull(request.sourceTitle()))
                .sourceUrl(trimToNull(request.sourceUrl()))
                .publishedYear(request.publishedYear())
                .displayOrder(getNextDisplayOrder(workId))
                .isFeatured(Boolean.TRUE.equals(request.isFeatured()))
                .isPublished(request.isPublished() == null || request.isPublished())
                .build();

        try {
            return commentaryMapper.toResponse(commentaryRepository.saveAndFlush(commentary));
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Không thể tạo bình phẩm với thứ tự hiện tại"
            );
        }
    }

    @Override
    @Transactional
    public WorkCommentaryResponse updateCommentary(
            UUID workId,
            UUID commentaryId,
            UpdateWorkCommentaryRequest request) {
        WorkCommentary commentary = requireCommentary(commentaryId);
        ensureCommentaryBelongsToWork(commentary, workId);

        if (request.title() != null) {
            commentary.setTitle(trimToNull(request.title()));
        }
        if (request.content() != null) {
            commentary.setContent(request.content().trim());
        }
        if (request.commentatorName() != null) {
            commentary.setCommentatorName(request.commentatorName().trim());
        }
        if (request.commentatorType() != null) {
            commentary.setCommentatorType(request.commentatorType());
        }
        if (request.sourceTitle() != null) {
            commentary.setSourceTitle(trimToNull(request.sourceTitle()));
        }
        if (request.sourceUrl() != null) {
            commentary.setSourceUrl(trimToNull(request.sourceUrl()));
        }
        if (request.publishedYear() != null) {
            commentary.setPublishedYear(request.publishedYear());
        }
        if (request.isFeatured() != null) {
            commentary.setIsFeatured(request.isFeatured());
        }
        if (request.isPublished() != null) {
            commentary.setIsPublished(request.isPublished());
        }

        return commentaryMapper.toResponse(commentaryRepository.save(commentary));
    }

    @Override
    @Transactional
    public void deleteCommentary(UUID workId, UUID commentaryId) {
        WorkCommentary commentary = requireCommentary(commentaryId);
        ensureCommentaryBelongsToWork(commentary, workId);
        commentaryRepository.delete(commentary);
    }

    private Work requireWork(UUID workId) {
        Objects.requireNonNull(workId, "workId không được để trống");
        return workRepository.findById(workId)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));
    }

    private void requireReadableWork(UUID workId) {
        WorkReadAccessValidator.validate(requireWork(workId));
    }

    private WorkCommentary requireCommentary(UUID commentaryId) {
        Objects.requireNonNull(commentaryId, "commentaryId không được để trống");
        return commentaryRepository.findByIdWithWork(commentaryId)
                .orElseThrow(() -> new ApiException(ErrorCode.COMMENTARY_NOT_FOUND));
    }

    private void ensureCommentaryBelongsToWork(WorkCommentary commentary, UUID workId) {
        Objects.requireNonNull(workId, "workId không được để trống");
        if (!workId.equals(commentary.getWork().getId())) {
            throw new ApiException(ErrorCode.COMMENTARY_NOT_FOUND);
        }
    }

    private int getNextDisplayOrder(UUID workId) {
        return commentaryRepository.findMaxDisplayOrderByWorkId(workId) + 1;
    }

    private String trimToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
