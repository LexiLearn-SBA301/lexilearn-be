package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkSectionRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkSectionRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSectionDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSectionSummaryResponse;
import com.sba.lexilearnbe.modules.work.entity.WorkSection;
import com.sba.lexilearnbe.modules.work.enums.WorkSectionContentType;
import com.sba.lexilearnbe.modules.work.event.WorkSyncRequestedEvent;
import com.sba.lexilearnbe.modules.work.mapper.WorkSectionMapper;
import com.sba.lexilearnbe.modules.work.repository.WorkSectionRepository;
import com.sba.lexilearnbe.modules.work.services.WorkSectionService;
import com.sba.lexilearnbe.modules.work.utils.WordCountCalculator;
import com.sba.lexilearnbe.modules.work.utils.WorkReadAccessValidator;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkSectionServiceImpl implements WorkSectionService {

    private final WorkRepository workRepository;
    private final WorkSectionRepository workSectionRepository;
    private final WorkSectionMapper workSectionMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(readOnly = true)
    public List<WorkSectionSummaryResponse> getSections(UUID workId) {
        requireReadableWork(workId);

        return workSectionRepository.findAllByWork_IdOrderByNumberAsc(workId)
                .stream()
                .map(workSectionMapper::toSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkSectionDetailResponse> getFullSections(UUID workId) {
        requireReadableWork(workId);

        return workSectionRepository.findAllByWork_IdOrderByNumberAsc(workId)
                .stream()
                .map(workSectionMapper::toDetail)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public WorkSectionDetailResponse getSection(UUID workId, UUID sectionId) {
        Objects.requireNonNull(workId, "workId không được để trống");

        WorkSection section = requireSection(sectionId);
        if (!workId.equals(section.getWork().getId())) {
            throw new ApiException(ErrorCode.SECTION_NOT_FOUND);
        }

        WorkReadAccessValidator.validate(section.getWork());

        return workSectionMapper.toDetail(section);
    }

    @Override
    @Transactional
    public WorkSectionDetailResponse createSection(UUID workId, CreateWorkSectionRequest request) {
        Work work = requireWork(workId);
        Integer sectionNumber = request.getNumber() != null
                ? request.getNumber()
                : getNextNumber(workId);

        validateUniqueNumber(workId, sectionNumber, null);

        WorkSection section = WorkSection.builder()
                .work(work)
                .number(sectionNumber)
                .title(request.getTitle().trim())
                .content(request.getContent())
                .contentType(resolveContentType(request.getContentType(), work))
                .wordCount(WordCountCalculator.count(request.getContent()))
                .build();

        try {
            WorkSection savedSection = workSectionRepository.saveAndFlush(section);
            publishWorkUpsertSync(workId);
            return workSectionMapper.toDetail(savedSection);
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Số thứ tự phần văn bản đã tồn tại trong tác phẩm"
            );
        }
    }

    @Override
    @Transactional
    public WorkSectionDetailResponse updateSection(UUID workId, UUID sectionId, UpdateWorkSectionRequest request) {
        Objects.requireNonNull(workId, "workId không được để trống");
        WorkSection section = requireSection(sectionId);
        ensureSectionBelongsToWork(section, workId);

        if (request.getNumber() != null) {
            validateUniqueNumber(workId, request.getNumber(), sectionId);
            section.setNumber(request.getNumber());
        }
        if (request.getTitle() != null) {
            section.setTitle(request.getTitle().trim());
        }
        if (request.getContent() != null) {
            section.setContent(request.getContent());
            section.setWordCount(WordCountCalculator.count(request.getContent()));
        }
        if (request.getContentType() != null) {
            section.setContentType(request.getContentType());
        }

        try {
            WorkSection savedSection = workSectionRepository.saveAndFlush(section);
            publishWorkUpsertSync(workId);
            return workSectionMapper.toDetail(savedSection);
        } catch (DataIntegrityViolationException exception) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Số thứ tự phần văn bản đã tồn tại trong tác phẩm"
            );
        }
    }

    @Override
    @Transactional
    public void deleteSection(UUID workId, UUID sectionId) {
        Objects.requireNonNull(workId, "workId không được để trống");
        WorkSection section = requireSection(sectionId);
        ensureSectionBelongsToWork(section, workId);

        workSectionRepository.delete(section);
        publishWorkUpsertSync(workId);
    }

    private Work requireWork(UUID workId) {
        Objects.requireNonNull(workId, "workId không được để trống");

        return workRepository.findById(workId)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));
    }

    private Work requireReadableWork(UUID workId) {
        Work work = requireWork(workId);
        WorkReadAccessValidator.validate(work);
        return work;
    }

    private WorkSection requireSection(UUID sectionId) {
        Objects.requireNonNull(sectionId, "sectionId không được để trống");

        return workSectionRepository.findByIdWithWork(sectionId)
                .orElseThrow(() -> new ApiException(ErrorCode.SECTION_NOT_FOUND));
    }

    private void ensureSectionBelongsToWork(WorkSection section, UUID workId) {
        if (!workId.equals(section.getWork().getId())) {
            throw new ApiException(ErrorCode.SECTION_NOT_FOUND);
        }
    }

    private void validateUniqueNumber(UUID workId, Integer number, UUID excludedSectionId) {
        boolean exists = excludedSectionId == null
                ? workSectionRepository.existsByWork_IdAndNumber(workId, number)
                : workSectionRepository.existsByWork_IdAndNumberAndIdNot(workId, number, excludedSectionId);

        if (exists) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Số thứ tự phần văn bản đã tồn tại trong tác phẩm"
            );
        }
    }

    private int getNextNumber(UUID workId) {
        return workSectionRepository.findMaxNumberByWorkId(workId) + 1;
    }

    private WorkSectionContentType resolveContentType(WorkSectionContentType contentType, Work work) {
        if (contentType != null) {
            return contentType;
        }
        if ("truyen_tho".equalsIgnoreCase(work.getGenre()) || "luc_bat".equalsIgnoreCase(work.getSubGenre())) {
            return WorkSectionContentType.POETRY;
        }
        return WorkSectionContentType.PROSE;
    }

    private void publishWorkUpsertSync(UUID workId) {
        eventPublisher.publishEvent(WorkSyncRequestedEvent.upsert(workId));
    }
}
