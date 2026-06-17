package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.request.AuthorRequest;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorSummaryResponse;
import com.sba.lexilearnbe.modules.work.entity.Author;
import com.sba.lexilearnbe.modules.work.mapper.AuthorMapper;
import com.sba.lexilearnbe.modules.work.repository.AuthorRepository;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.work.services.AuthorService;
import com.sba.lexilearnbe.modules.work.utils.SlugUtils;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final WorkRepository workRepository;

    @Override
    public Page<AuthorSummaryResponse> getAuthors(String searchKeyword, Pageable pageable) {
        String safeSearch = (searchKeyword == null) ? "" : searchKeyword;
        return authorRepository.findAuthorsWithFilter(safeSearch, pageable)
                .map(authorMapper::toSummaryResponse);
    }

    @Override
    public AuthorDetailResponse getAuthorDetail(String slug) {
        return authorMapper.toDetailResponse(
                authorRepository.findBySlug(slug)
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

        Author updatedAuthor = authorRepository.save(author);
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
        authorRepository.delete(author);
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
}