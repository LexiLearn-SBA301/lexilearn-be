package com.sba.lexilearnbe.modules.workdetail.services.impl;

import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.workdetail.dto.request.CreateWorkSectionRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.request.UpdateWorkSectionRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkSectionDetailResponse;
import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkSectionSummaryResponse;
import com.sba.lexilearnbe.modules.workdetail.entity.WorkSection;
import com.sba.lexilearnbe.modules.workdetail.mapper.WorkSectionMapper;
import com.sba.lexilearnbe.modules.workdetail.repository.WorkSectionRepository;
import com.sba.lexilearnbe.modules.workdetail.services.WorkSectionService;
import com.sba.lexilearnbe.modules.workdetail.util.WordCountCalculator;
import com.sba.lexilearnbe.modules.workdetail.util.WorkReadAccessValidator;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkSectionServiceImpl implements WorkSectionService {

    private final WorkRepository workRepository;
    private final WorkSectionRepository workSectionRepository;
    private final WorkSectionMapper workSectionMapper;

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
    public WorkSectionDetailResponse getSection(UUID sectionId) {
        WorkSection section = requireSection(sectionId);
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
                .wordCount(WordCountCalculator.count(request.getContent()))
                .build();

        return workSectionMapper.toDetail(workSectionRepository.save(section));
    }

    @Override
    @Transactional
    public WorkSectionDetailResponse updateSection(UUID sectionId, UpdateWorkSectionRequest request) {
        WorkSection section = requireSection(sectionId);

        if (request.getNumber() != null) {
            validateUniqueNumber(section.getWork().getId(), request.getNumber(), sectionId);
            section.setNumber(request.getNumber());
        }
        if (request.getTitle() != null) {
            section.setTitle(request.getTitle().trim());
        }
        if (request.getContent() != null) {
            section.setContent(request.getContent());
            section.setWordCount(WordCountCalculator.count(request.getContent()));
        }

        return workSectionMapper.toDetail(workSectionRepository.save(section));
    }

    @Override
    @Transactional
    public void deleteSection(UUID sectionId) {
        workSectionRepository.delete(requireSection(sectionId));
    }

    private Work requireWork(UUID workId) {
        return workRepository.findById(workId)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));
    }

    private Work requireReadableWork(UUID workId) {
        Work work = requireWork(workId);
        WorkReadAccessValidator.validate(work);
        return work;
    }

    private WorkSection requireSection(UUID sectionId) {
        return workSectionRepository.findById(sectionId)
                .orElseThrow(() -> new ApiException(ErrorCode.SECTION_NOT_FOUND));
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
}
