package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkCommentaryResponse;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.entity.WorkCommentary;
import com.sba.lexilearnbe.modules.work.event.WorkSyncRequestedEvent;
import com.sba.lexilearnbe.modules.work.mapper.WorkCommentaryMapper;
import com.sba.lexilearnbe.modules.work.repository.WorkCommentaryRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.work.services.WorkCommentaryService;
import com.sba.lexilearnbe.modules.work.utils.WorkReadAccessValidator;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkCommentaryServiceImpl implements WorkCommentaryService {

    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "displayOrder",
            "createdAt",
            "publishedYear",
            "commentatorName"
    );

    private final WorkRepository workRepository;
    private final WorkCommentaryRepository commentaryRepository;
    private final WorkCommentaryMapper commentaryMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public Page<WorkCommentaryResponse> getPublishedCommentaries(UUID workId, int page, int size, String sortDir, String sortBy) {
        Pageable pageable = createPageable(page, size, sortDir, sortBy);
        requireReadableWork(workId);
        return commentaryRepository.findPublishedByWorkId(workId, pageable)
                .map(commentaryMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<WorkCommentaryResponse> getAllCommentaries(UUID workId, int page, int size, String sortDir, String sortBy) {
        Pageable pageable = createPageable(page, size, sortDir, sortBy);
        requireWork(workId);
        return commentaryRepository.findAllByWorkId(workId, pageable)
                .map(commentaryMapper::toResponse);
    }

    @Override
    @Transactional
    public WorkCommentaryResponse createCommentary(UUID workId, CreateWorkCommentaryRequest request) {
        Work work = requireWork(workId);
        WorkCommentary commentary = commentaryMapper.toEntity(request);
        commentary.setWork(work);
        commentary.setDisplayOrder(getNextDisplayOrder(workId));

        try {
            WorkCommentary savedCommentary = commentaryRepository.saveAndFlush(commentary);
            publishWorkUpsertSync(workId);
            return commentaryMapper.toResponse(savedCommentary);
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Không thể tạo bình phẩm với thứ tự hiện tại"
            );
        }
    }

    @Override
    @Transactional
    public WorkCommentaryResponse updateCommentary(UUID workId, UUID commentaryId, UpdateWorkCommentaryRequest request) {
        WorkCommentary commentary = requireCommentary(commentaryId);
        ensureCommentaryBelongsToWork(commentary, workId);

        commentaryMapper.updateEntityFromRequest(request, commentary);

        WorkCommentary savedCommentary = commentaryRepository.save(commentary);
        publishWorkUpsertSync(workId);
        return commentaryMapper.toResponse(savedCommentary);
    }

    @Override
    @Transactional
    public void deleteCommentary(UUID workId, UUID commentaryId) {
        WorkCommentary commentary = requireCommentary(commentaryId);
        ensureCommentaryBelongsToWork(commentary, workId);
        commentaryRepository.delete(commentary);
        publishWorkUpsertSync(workId);
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

    private Pageable createPageable(int page, int size, String sortDir, String sortBy) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Tham số phân trang không hợp lệ");
        }
        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
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
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }

    private void publishWorkUpsertSync(UUID workId) {
        eventPublisher.publishEvent(WorkSyncRequestedEvent.upsert(workId));
    }
}
