package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.response.WorkDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSummaryResponse;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.work.services.WorkService;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService {

    private final WorkRepository workRepository;

    @Override
    public Page<WorkSummaryResponse> getWorksByFilter(String genre, String period, String searchKeyword, int page, int size, String sort) {

        String[] sortParams = sort.split(",");
        Sort.Direction direction = (sortParams.length > 1 && sortParams[1].equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        String sortBy = sortParams[0];
        if ("viewCount".equalsIgnoreCase(sortBy)) {
            sortBy = "view_count";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Work> worksPage = workRepository.findWorksWithFilter(genre, period, searchKeyword, pageable);

        return worksPage.map(work -> WorkSummaryResponse.builder()
                .id(work.getId())
                .slug(work.getSlug())
                .title(work.getTitle())
                .authorName(work.getAuthor() != null ? work.getAuthor().getName() : "Khuyết danh")
                .coverUrl(work.getCoverUrl())
                .subGenre(work.getSubGenre())
                .famousQuote(work.getFamousQuote())
                .tags(new ArrayList<>())
                .build());
    }

    @Override
    public WorkDetailResponse getWorkDetail(String slug) {
        // Tìm tác phẩm theo slug, nếu không có thì bắn Exception
        Work work = workRepository.findBySlug(slug)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));

        // Map toàn bộ dữ liệu chi tiết
        return WorkDetailResponse.builder()
                .id(work.getId())
                .title(work.getTitle())
                .originalTitle(work.getOriginalTitle())
                .slug(work.getSlug())
                .authorId(work.getAuthor() != null ? work.getAuthor().getId() : null)
                .authorName(work.getAuthor() != null ? work.getAuthor().getName() : "Khuyết danh")
                .authorSlug(work.getAuthor() != null ? work.getAuthor().getSlug() : null)
                .genre(work.getGenre())
                .subGenre(work.getSubGenre())
                .period(work.getPeriod())
                .grade(work.getGrade())
                .semester(work.getSemester())
                .publishYear(work.getPublishYear())
                .summary(work.getSummary())
                .coverUrl(work.getCoverUrl())
                .viewCount(work.getViewCount())
                .historicalContext(work.getHistoricalContext())
                .realisticValue(work.getRealisticValue())
                .humanisticValue(work.getHumanisticValue())
                .artisticValue(work.getArtisticValue())
                .famousQuote(work.getFamousQuote())
                .quoteAttribution(work.getQuoteAttribution())
                .tags(new ArrayList<>())
                .updatedAt(work.getUpdatedAt())
                .build();
    }
}