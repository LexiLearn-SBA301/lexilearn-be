package com.sba.lexilearnbe.modules.reading.dto.response;

import com.sba.lexilearnbe.modules.reading.enums.NoteColor;

import java.time.LocalDateTime;
import java.util.UUID;

public record NoteResponse(
        UUID id,
        UUID sectionId,
        Integer startOffset,
        Integer endOffset,
        String highlightedText,
        String userNote,
        NoteColor color,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
