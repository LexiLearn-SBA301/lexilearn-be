package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.request.AuthorRequest;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorSummaryResponse;
import com.sba.lexilearnbe.modules.work.entity.Author;
import com.sba.lexilearnbe.modules.work.event.WorkSyncRequestedEvent;
import com.sba.lexilearnbe.modules.work.mapper.AuthorMapper;
import com.sba.lexilearnbe.modules.work.repository.AuthorRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.work.services.AuthorService;
import com.sba.lexilearnbe.modules.work.utils.SlugUtils;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import com.sba.lexilearnbe.shared.infrastructure.storage.ImageStorageService;
import com.sba.lexilearnbe.shared.infrastructure.storage.ImageStorageTransactionManager;
import com.sba.lexilearnbe.shared.infrastructure.storage.ImageUploadTarget;
import com.sba.lexilearnbe.shared.infrastructure.storage.StoredImage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final WorkRepository workRepository;
    private final ImageStorageService imageStorageService;
    private final ImageStorageTransactionManager imageStorageTransactionManager;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<AuthorSummaryResponse> getAuthors(String searchKeyword, String period, Pageable pageable) {
        String safeSearch = StringUtils.hasText(searchKeyword) ? searchKeyword.trim() : "";
        String safePeriod = StringUtils.hasText(period) ? period.trim() : "";
        return authorRepository.findAuthorsWithFilter(safeSearch, safePeriod,pageable)
                .map(authorMapper::toSummaryResponse);
    }

    @Override
    public AuthorDetailResponse getAuthorDetail(String slug) {
        if (!StringUtils.hasText(slug)) {
            throw new ApiException(ErrorCode.AUTHOR_NOT_FOUND);
        }
        return authorMapper.toDetailResponse(
                authorRepository.findBySlug(slug.trim())
                        .orElseThrow(() -> new ApiException(ErrorCode.AUTHOR_NOT_FOUND))
        );
    }

    @Override
    @Transactional
    public AuthorDetailResponse createAuthor(AuthorRequest request) {
        validateAuthorYears(request.getBirthYear(), request.getDeathYear());

        String slug = SlugUtils.generateSlug(request.getName());
        if (authorRepository.existsBySlug(slug)) {
            throw new ApiException(ErrorCode.AUTHOR_ALREADY_EXISTS);
        }

        Author author = authorMapper.toEntity(request);
        author.setSlug(slug);
        if (request.getPortrait() != null) {
            StoredImage storedImage = imageStorageService.verifyUploadedImage(
                    request.getPortrait(),
                    ImageUploadTarget.AUTHOR_PORTRAIT
            );
            author.setPortraitUrl(storedImage.url());
            author.setPortraitPublicId(storedImage.publicId());
            imageStorageTransactionManager.scheduleCreate(storedImage.publicId());
        }

        try {
            Author savedAuthor = authorRepository.save(author);
            return authorMapper.toDetailResponse(savedAuthor);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorCode.AUTHOR_ALREADY_EXISTS);
        }
    }
    @Override
    @Transactional
    public AuthorDetailResponse updateAuthor(UUID id, AuthorRequest request) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.AUTHOR_NOT_FOUND));
        validateAuthorYears(request.getBirthYear(), request.getDeathYear());

        authorMapper.updateEntityFromRequest(request, author);
        if (request.getPortrait() != null) {
            String oldPublicId = author.getPortraitPublicId();
            StoredImage storedImage = imageStorageService.verifyUploadedImage(
                    request.getPortrait(),
                    ImageUploadTarget.AUTHOR_PORTRAIT
            );
            author.setPortraitUrl(storedImage.url());
            author.setPortraitPublicId(storedImage.publicId());
            imageStorageTransactionManager.scheduleReplacement(oldPublicId, storedImage.publicId());
        }

        Author updatedAuthor = authorRepository.save(author);
        publishAuthorWorksUpsertSync(updatedAuthor.getId());
        return authorMapper.toDetailResponse(updatedAuthor);
    }

    @Override
    @Transactional
    public AuthorDetailResponse deletePortrait(UUID id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.AUTHOR_NOT_FOUND));

        String oldPublicId = author.getPortraitPublicId();
        author.setPortraitUrl(null);
        author.setPortraitPublicId(null);
        Author updatedAuthor = authorRepository.save(author);
        imageStorageTransactionManager.scheduleDeletion(oldPublicId);
        publishAuthorWorksUpsertSync(updatedAuthor.getId());
        return authorMapper.toDetailResponse(updatedAuthor);
    }

    @Override
    @Transactional
    public void deleteAuthor(UUID id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.AUTHOR_NOT_FOUND));
        if (workRepository.existsByAuthorId(id)) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR,
                    "Không thể xoá tác giả này vì đã có tác phẩm liên kết trên hệ thống");
        }
        String portraitPublicId = author.getPortraitPublicId();
        authorRepository.delete(author);
        imageStorageTransactionManager.scheduleDeletion(portraitPublicId);
    }
    private void validateAuthorYears(Integer birthYear, Integer deathYear) {
        int currentYear = java.time.Year.now().getValue(); // Tự động lấy năm hiện tại (2026)

        if (birthYear != null && birthYear > currentYear) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Năm sinh không được lớn hơn năm hiện tại");
        }

        if (deathYear != null && deathYear > currentYear) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Năm mất không được lớn hơn năm hiện tại");
        }

        if (birthYear != null && deathYear != null && deathYear < birthYear) {
            throw new ApiException(ErrorCode.VALIDATION_ERROR, "Năm mất không được nhỏ hơn năm sinh");
        }
    }

    private void publishAuthorWorksUpsertSync(UUID authorId) {
        workRepository.findIdsByAuthorId(authorId)
                .forEach(workId -> eventPublisher.publishEvent(WorkSyncRequestedEvent.upsert(workId)));
    }
}
