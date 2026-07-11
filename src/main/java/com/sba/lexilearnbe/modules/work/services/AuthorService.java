package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.request.AuthorRequest;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface AuthorService {
    Page<AuthorSummaryResponse> getAuthors(String searchKeyword, String period, Pageable pageable);
    AuthorDetailResponse getAuthorDetail(String slug);
    AuthorDetailResponse createAuthor(AuthorRequest request);
    AuthorDetailResponse updateAuthor(UUID id, AuthorRequest request);
    AuthorDetailResponse deletePortrait(UUID id);
    void deleteAuthor(UUID id);
}
