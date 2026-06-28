package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.request.WorkRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSummaryResponse;
import com.sba.lexilearnbe.modules.work.entity.Author;
import com.sba.lexilearnbe.modules.work.entity.Tag;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.mapper.WorkMapper;
import com.sba.lexilearnbe.modules.work.repository.AuthorRepository;
import com.sba.lexilearnbe.modules.work.repository.TagRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.work.services.WorkService;
import com.sba.lexilearnbe.modules.work.utils.SlugUtils;
import com.sba.lexilearnbe.modules.work.repository.WorkSectionRepository;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import com.sba.lexilearnbe.shared.infrastructure.storage.ImageStorageService;
import com.sba.lexilearnbe.shared.infrastructure.storage.ImageStorageTransactionManager;
import com.sba.lexilearnbe.shared.infrastructure.storage.ImageUploadTarget;
import com.sba.lexilearnbe.shared.infrastructure.storage.StoredImage;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.sba.lexilearnbe.modules.work.specification.WorkSpecification;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService {

    private final AuthorRepository authorRepository;
    private final WorkRepository workRepository;
    private final WorkMapper workMapper;
    private final WorkSectionRepository workSectionRepository;
    private final TagRepository tagRepository;
    private final ImageStorageService imageStorageService;
    private final ImageStorageTransactionManager imageStorageTransactionManager;

    @Override
    public Page<WorkSummaryResponse> getWorksByFilter(String genre, String period, String searchKeyword, String tag, Pageable pageable) {
        Specification<Work> spec = WorkSpecification.filterWorks(genre, period, tag, searchKeyword);

        Page<Work> worksPage = workRepository.findAll(spec, pageable);

        if (worksPage.isEmpty()) {
            return Page.empty(pageable);
        }

        List<UUID> workIds = worksPage.getContent().stream()
                .map(Work::getId)
                .toList();

        List<Work> fullyFetchedWorks = workRepository.findAllByIdIn(workIds);

        Map<UUID, Work> workMap = fullyFetchedWorks.stream()
                .collect(Collectors.toMap(Work::getId, w -> w));

        List<WorkSummaryResponse> responseList = worksPage.getContent().stream()
                .map(work -> {
                    Work fullWork = workMap.get(work.getId());
                    return workMapper.toSummaryResponse(fullWork, fullWork.getTags());
                })
                .toList();

        return new PageImpl<>(responseList, pageable, worksPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    @Override
    public WorkDetailResponse getWorkDetail(String slug) {
        if (!StringUtils.hasText(slug)) {
            throw new ApiException(ErrorCode.WORK_NOT_FOUND);
        }
        return workMapper.toDetailResponse(
                workRepository.findPublishedBySlug(slug.trim())
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
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            work.setTags(tags);
        }
        if (request.getCover() != null) {
            StoredImage storedImage = imageStorageService.verifyUploadedImage(
                    request.getCover(),
                    ImageUploadTarget.WORK_COVER
            );
            work.setCoverUrl(storedImage.url());
            work.setCoverPublicId(storedImage.publicId());
            imageStorageTransactionManager.scheduleCreate(storedImage.publicId());
        }

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

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            Set<Tag> tags = new HashSet<>(tagRepository.findAllById(request.getTagIds()));
            work.setTags(tags);
        } else {
            work.getTags().clear();
        }
        if (request.getCover() != null) {
            String oldPublicId = work.getCoverPublicId();
            StoredImage storedImage = imageStorageService.verifyUploadedImage(
                    request.getCover(),
                    ImageUploadTarget.WORK_COVER
            );
            work.setCoverUrl(storedImage.url());
            work.setCoverPublicId(storedImage.publicId());
            imageStorageTransactionManager.scheduleReplacement(oldPublicId, storedImage.publicId());
        }
        Work updatedWork = workRepository.save(work);
        return workMapper.toDetailResponse(updatedWork);
    }

    @Override
    @Transactional
    public WorkDetailResponse deleteCover(UUID id) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));

        String oldPublicId = work.getCoverPublicId();
        work.setCoverUrl(null);
        work.setCoverPublicId(null);
        Work updatedWork = workRepository.save(work);
        imageStorageTransactionManager.scheduleDeletion(oldPublicId);
        return workMapper.toDetailResponse(updatedWork);
    }

    @Override
    @Transactional
    public void deleteWork(UUID id) {
        Work work = workRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));
        String coverPublicId = work.getCoverPublicId();
        workSectionRepository.deleteByWorkId(id);
        workRepository.delete(work);
        imageStorageTransactionManager.scheduleDeletion(coverPublicId);
    }

}
