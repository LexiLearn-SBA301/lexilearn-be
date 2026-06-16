package com.sba.lexilearnbe.modules.workdetail.mapper;

import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkCharacterResponse;
import com.sba.lexilearnbe.modules.workdetail.entity.WorkCharacter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkCharacterMapper {

    @Mapping(target = "workId", source = "work.id")
    WorkCharacterResponse toResponse(WorkCharacter character);
}
