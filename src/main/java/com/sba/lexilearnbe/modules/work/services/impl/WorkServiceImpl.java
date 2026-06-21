package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.request.WorkRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSummaryResponse;
import com.sba.lexilearnbe.modules.work.entity.Author;
import com.sba.lexilearnbe.modules.work.entity.Tag;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.mapper.WorkMapper;
import com.sba.lexilearnbe.modules.work.repository.AuthorRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.work.services.WorkService;
import com.sba.lexilearnbe.modules.work.utils.SlugUtils;
import com.sba.lexilearnbe.modules.workdetail.entity.WorkSection;
import com.sba.lexilearnbe.modules.workdetail.repository.WorkSectionRepository;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService {

    private final AuthorRepository authorRepository;
    private final WorkRepository workRepository;
    private final WorkMapper workMapper;
    private final WorkSectionRepository workSectionRepository;

    @Override
    public Page<WorkSummaryResponse> getWorksByFilter(String genre, String period, String searchKeyword, Pageable pageable) {
        String safeGenre = StringUtils.hasText(genre) ? genre.trim() : null;
        String safePeriod = StringUtils.hasText(period) ? period.trim() : null;
        String safeSearch = StringUtils.hasText(searchKeyword) ? searchKeyword.trim() : "";
        Page<Work> worksPage = workRepository.findWorksWithFilter(safeGenre, safePeriod, safeSearch, pageable);
        if (worksPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<Work> worksWithTags = workRepository.fetchTagsForWorks(worksPage.getContent());
        Map<UUID, Set<Tag>> tagsMap = worksWithTags.stream()
                .collect(Collectors.toMap(Work::getId, Work::getTags));

        return worksPage.map(work ->
                workMapper.toSummaryResponse(work, tagsMap.getOrDefault(work.getId(), Collections.emptySet()))
        );
    }

    @Transactional
    @Override
    public WorkDetailResponse getWorkDetail(String slug) {
        if (!StringUtils.hasText(slug)) {
            throw new ApiException(ErrorCode.WORK_NOT_FOUND);
        }
        workRepository.incrementViewCountBySlug(slug.trim());
        return workMapper.toDetailResponse(
                workRepository.findBySlug(slug.trim())
                        .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND))
        );
    }
    @Override
    @Transactional
    public WorkDetailResponse createWork(WorkRequest request) {
        // 1. Validate xem tác giả truyền lên có thực sự tồn tại không
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ApiException(ErrorCode.AUTHOR_NOT_FOUND));

        String slug = SlugUtils.generateSlug(request.getTitle());

        if (workRepository.existsBySlug(slug)) {
            throw new ApiException(ErrorCode.WORK_ALREADY_EXISTS);
        }

        Work work = workMapper.toEntity(request);
        work.setAuthor(author);
        work.setSlug(slug);

        try {
            Work savedWork = workRepository.save(work);
            return workMapper.toDetailResponse(savedWork);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorCode.WORK_ALREADY_EXISTS);
        }
    }

    @Override
    @Transactional
    public WorkDetailResponse updateWork(UUID id, WorkRequest request) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));
        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ApiException(ErrorCode.AUTHOR_NOT_FOUND));

        workMapper.updateEntityFromRequest(request, work);
        work.setAuthor(author);

        Work updatedWork = workRepository.save(work);
        return workMapper.toDetailResponse(updatedWork);
    }

    @Override
    @Transactional
    public void deleteWork(UUID id) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));
        workSectionRepository.deleteByWorkId(id);
        workRepository.delete(work);
    }

}