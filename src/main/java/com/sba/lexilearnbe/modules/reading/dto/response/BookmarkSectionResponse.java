package com.sba.lexilearnbe.modules.reading.dto.response;

import java.util.UUID;

public record BookmarkSectionResponse(
        UUID id,
        Integer number,
        String title
) {
}
