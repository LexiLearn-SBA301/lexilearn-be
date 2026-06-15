package com.sba.lexilearnbe.modules.work.services.impl;

import com.sba.lexilearnbe.modules.work.dto.response.WorkDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSummaryResponse;
import com.sba.lexilearnbe.modules.work.entity.Tag;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.work.services.WorkService;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkServiceImpl implements WorkService {

    private final WorkRepository workRepository;

    @Override
    public Page<WorkSummaryResponse> getWorksByFilter(String genre, String period, String searchKeyword, Pageable pageable) {
        Page<Work> worksPage = workRepository.findWorksWithFilter(genre, period, searchKeyword, pageable);
        if (worksPage.isEmpty()) {
            return Page.empty(pageable);
        }
        List<Work> worksWithTags = workRepository.fetchTagsForWorks(worksPage.getContent());
        Map<UUID, Set<Tag>> tagsMap = worksWithTags.stream()
                .collect(Collectors.toMap(Work::getId, Work::getTags));

        return worksPage.map(work -> WorkSummaryResponse.builder()
                .id(work.getId())
                .slug(work.getSlug())
                .title(work.getTitle())
                .authorName(work.getAuthor() != null ? work.getAuthor().getName() : "Khuyết danh")
                .tags(tagsMap.getOrDefault(work.getId(), Collections.emptySet())
                        .stream().map(Tag::getName).toList())
                .build());
    }

    @Override
    public WorkDetailResponse getWorkDetail(String slug) {
        Work work = workRepository.findBySlug(slug)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));

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
                .tags(work.getTags().stream().map(Tag::getName).toList())
                .updatedAt(work.getUpdatedAt())
                .build();
    }
}