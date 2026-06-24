package com.sba.lexilearnbe.modules.reading.dto.response;

import java.util.UUID;

public record BookmarkWorkResponse(
        UUID id,
        String slug,
        String title,
        String coverUrl,
        String authorName
) {
}
