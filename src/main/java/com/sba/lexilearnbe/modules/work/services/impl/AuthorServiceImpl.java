package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.response.AuthorDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorSummaryResponse;
import com.sba.lexilearnbe.modules.work.entity.Author;
import com.sba.lexilearnbe.modules.work.repository.AuthorRepository;
import com.sba.lexilearnbe.modules.work.services.AuthorService;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public Page<AuthorSummaryResponse> getAuthors(String searchKeyword, int page, int size, String sort) {
        String[] sortParams = sort.split(",");
        Sort.Direction direction = (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        // XỬ LÝ LỖI Ở ĐÂY: Nếu searchKeyword là null, đổi thành chuỗi rỗng ""
        String safeSearch = (searchKeyword == null) ? "" : searchKeyword;

        // Truyền safeSearch vào
        Page<Author> authorsPage = authorRepository.findAuthorsWithFilter(safeSearch, pageable);

        return authorsPage.map(author -> AuthorSummaryResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .slug(author.getSlug())
                .portraitUrl(author.getPortraitUrl())
                .period(author.getPeriod())
                .bio(author.getBio())
                .birthYear(author.getBirthYear())
                .deathYear(author.getDeathYear())
                .build());
    }
    @Override
    public AuthorDetailResponse getAuthorDetail(String slug) {
        Author author = authorRepository.findBySlug(slug)
                .orElseThrow(() -> new ApiException(ErrorCode.AUTHOR_NOT_FOUND));

        return AuthorDetailResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .penName(author.getPenName())
                .slug(author.getSlug())
                .birthYear(author.getBirthYear())
                .deathYear(author.getDeathYear())
                .period(author.getPeriod())
                .bio(author.getBio())
                .portraitUrl(author.getPortraitUrl())
                .build();
    }
}