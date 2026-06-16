package com.sba.lexilearnbe.modules.workdetail.mapper;

import com.sba.lexilearnbe.modules.workdetail.dto.response.ArtisticFeatureResponse;
import com.sba.lexilearnbe.modules.workdetail.entity.ArtisticFeature;

public final class ArtisticFeatureMapper {

    private ArtisticFeatureMapper() {
    }

    public static ArtisticFeatureResponse toResponse(ArtisticFeature feature) {
        return ArtisticFeatureResponse.builder()
                .id(feature.getId())
                .workId(feature.getWork().getId())
                .title(feature.getTitle())
                .content(feature.getContent())
                .displayOrder(feature.getDisplayOrder())
                .createdAt(feature.getCreatedAt())
                .updatedAt(feature.getUpdatedAt())
                .build();
    }
}
