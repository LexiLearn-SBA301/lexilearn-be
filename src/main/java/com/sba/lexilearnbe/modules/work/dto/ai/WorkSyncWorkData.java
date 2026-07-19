package com.sba.lexilearnbe.modules.work.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record WorkSyncWorkData(
        UUID id,
        String title,
        String slug,

        @JsonProperty("original_title")
        String originalTitle,

        String genre,

        @JsonProperty("sub_genre")
        String subGenre,

        String period,
        Integer grade,
        Integer semester,

        @JsonProperty("publish_year")
        Integer publishYear,

        String summary,

        @JsonProperty("cover_url")
        String coverUrl,

        @JsonProperty("is_published")
        Boolean isPublished,

        @JsonProperty("historical_context")
        String historicalContext,

        @JsonProperty("realistic_value")
        String realisticValue,

        @JsonProperty("humanistic_value")
        String humanisticValue,

        @JsonProperty("artistic_value")
        String artisticValue,

        @JsonProperty("famous_quote")
        String famousQuote,

        @JsonProperty("quote_attribution")
        String quoteAttribution
) {
}
