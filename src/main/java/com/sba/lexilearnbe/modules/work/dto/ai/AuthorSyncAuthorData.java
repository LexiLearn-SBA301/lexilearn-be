package com.sba.lexilearnbe.modules.work.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record AuthorSyncAuthorData(
        UUID id,
        String name,

        @JsonProperty("pen_name")
        String penName,

        String slug,

        @JsonProperty("birth_year")
        Integer birthYear,

        @JsonProperty("death_year")
        Integer deathYear,

        String period,
        String bio,

        @JsonProperty("portrait_url")
        String portraitUrl
) {
}
