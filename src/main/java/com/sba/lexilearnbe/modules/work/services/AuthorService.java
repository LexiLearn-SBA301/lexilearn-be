package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.response.AuthorDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService {
    Page<AuthorSummaryResponse> getAuthors(String searchKeyword, Pageable pageable);
    AuthorDetailResponse getAuthorDetail(String slug);
}