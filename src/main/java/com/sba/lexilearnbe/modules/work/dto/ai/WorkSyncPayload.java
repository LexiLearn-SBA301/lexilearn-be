package com.sba.lexilearnbe.modules.work.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

public record WorkSyncPayload(
        @JsonProperty("schema_version")
        String schemaVersion,

        @JsonProperty("synced_at")
        OffsetDateTime syncedAt,

        WorkSyncWorkData work,

        @JsonProperty("author_ref")
        WorkSyncAuthorRefData authorRef,

        List<WorkSyncSectionData> sections,
        List<WorkSyncCommentaryData> commentaries,
        List<WorkSyncTagData> tags
) {
}
