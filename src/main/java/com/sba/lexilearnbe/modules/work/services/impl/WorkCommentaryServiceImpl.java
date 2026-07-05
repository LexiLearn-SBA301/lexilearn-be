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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Page<WorkCommentaryResponse> getPublishedCommentaries(
            UUID workId,
            Pageable pageable) {
        requireReadableWork(workId);
        return commentaryRepository.findPublishedByWorkId(workId, pageable)
                .map(commentaryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkCommentaryResponse> getAllCommentaries(
            UUID workId,
            Pageable pageable) {
        requireWork(workId);
        return commentaryRepository.findAllByWorkId(workId, pageable)
                .map(commentaryMapper::toResponse);
    }

    @Override
    @Transactional
    public WorkCommentaryResponse createCommentary(
            UUID workId,
            CreateWorkCommentaryRequest request) {
        Work work = requireWork(workId);
        WorkCommentary commentary = commentaryMapper.toEntity(request);
        commentary.setWork(work);
        commentary.setDisplayOrder(getNextDisplayOrder(workId));

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

        commentaryMapper.updateEntityFromRequest(request, commentary);

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
}
