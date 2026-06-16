package com.sba.lexilearnbe.modules.workdetail.mapper;

import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkCharacterResponse;
import com.sba.lexilearnbe.modules.workdetail.entity.WorkCharacter;

public final class WorkCharacterMapper {

    private WorkCharacterMapper() {
    }

    public static WorkCharacterResponse toResponse(WorkCharacter character) {
        return WorkCharacterResponse.builder()
                .id(character.getId())
                .workId(character.getWork().getId())
                .name(character.getName())
                .description(character.getDescription())
                .analysis(character.getAnalysis())
                .displayOrder(character.getDisplayOrder())
                .createdAt(character.getCreatedAt())
                .updatedAt(character.getUpdatedAt())
                .build();
    }
}
