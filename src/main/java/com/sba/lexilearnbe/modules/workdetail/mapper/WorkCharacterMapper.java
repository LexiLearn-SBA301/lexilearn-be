package com.sba.lexilearnbe.modules.workdetail.mapper;

import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkCharacterResponse;
import com.sba.lexilearnbe.modules.workdetail.entity.WorkCharacter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkCharacterMapper {

    @Mapping(target = "workId", source = "work.id")
    WorkCharacterResponse toResponse(WorkCharacter character);
}
