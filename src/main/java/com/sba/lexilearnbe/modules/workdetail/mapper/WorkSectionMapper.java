package com.sba.lexilearnbe.modules.workdetail.mapper;

import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkSectionDetailResponse;
import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkSectionSummaryResponse;
import com.sba.lexilearnbe.modules.workdetail.entity.WorkSection;

public final class WorkSectionMapper {

    private WorkSectionMapper() {
    }

    public static WorkSectionSummaryResponse toSummary(WorkSection section) {
        return WorkSectionSummaryResponse.builder()
                .id(section.getId())
                .workId(section.getWork().getId())
                .number(section.getNumber())
                .title(section.getTitle())
                .wordCount(section.getWordCount())
                .build();
    }

    public static WorkSectionDetailResponse toDetail(WorkSection section) {
        return WorkSectionDetailResponse.builder()
                .id(section.getId())
                .workId(section.getWork().getId())
                .number(section.getNumber())
                .title(section.getTitle())
                .content(section.getContent())
                .wordCount(section.getWordCount())
                .createdAt(section.getCreatedAt())
                .updatedAt(section.getUpdatedAt())
                .build();
    }
}
