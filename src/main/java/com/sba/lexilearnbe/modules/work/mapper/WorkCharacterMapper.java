package com.sba.lexilearnbe.modules.work.mapper;

import com.sba.lexilearnbe.modules.work.dto.response.WorkCharacterResponse;
import com.sba.lexilearnbe.modules.work.entity.WorkCharacter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkCharacterMapper {

    @Mapping(target = "workId", source = "work.id")
    WorkCharacterResponse toResponse(WorkCharacter character);
}
