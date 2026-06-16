package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.response.AuthorDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorSummaryResponse;
import com.sba.lexilearnbe.modules.work.entity.Author;
import com.sba.lexilearnbe.modules.work.mapper.AuthorMapper;
import com.sba.lexilearnbe.modules.work.repository.AuthorRepository;
import com.sba.lexilearnbe.modules.work.services.AuthorService;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;

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
}