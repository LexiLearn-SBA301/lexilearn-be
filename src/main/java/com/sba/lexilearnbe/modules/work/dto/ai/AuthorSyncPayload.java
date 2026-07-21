package com.sba.lexilearnbe.modules.work.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record AuthorSyncPayload(
        @JsonProperty("schema_version")
        String schemaVersion,

        @JsonProperty("synced_at")
        OffsetDateTime syncedAt,

        AuthorSyncAuthorData author
) {
}
