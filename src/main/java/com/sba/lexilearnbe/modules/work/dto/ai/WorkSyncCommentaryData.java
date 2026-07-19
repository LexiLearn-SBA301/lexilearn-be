package com.sba.lexilearnbe.modules.work.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record WorkSyncCommentaryData(
        UUID id,
        String title,
        String content,

        @JsonProperty("commentator_name")
        String commentatorName,

        @JsonProperty("commentator_type")
        String commentatorType,

        @JsonProperty("source_title")
        String sourceTitle,

        @JsonProperty("source_url")
        String sourceUrl,

        @JsonProperty("published_year")
        Integer publishedYear,

        @JsonProperty("display_order")
        Integer displayOrder,

        @JsonProperty("is_featured")
        Boolean isFeatured,

        @JsonProperty("is_published")
        Boolean isPublished
) {
}
