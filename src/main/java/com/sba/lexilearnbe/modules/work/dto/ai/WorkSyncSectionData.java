package com.sba.lexilearnbe.modules.work.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record WorkSyncSectionData(
        UUID id,
        Integer number,
        String title,
        String content,

        @JsonProperty("content_type")
        String contentType,

        @JsonProperty("word_count")
        Integer wordCount
) {
}
