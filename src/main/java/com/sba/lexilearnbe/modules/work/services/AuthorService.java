package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.response.AuthorDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorSummaryResponse;
import org.springframework.data.domain.Page;

public interface AuthorService {
    Page<AuthorSummaryResponse> getAuthors(String searchKeyword, int page, int size, String sort);
    AuthorDetailResponse getAuthorDetail(String slug);
}