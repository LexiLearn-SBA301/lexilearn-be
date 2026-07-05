package com.sba.lexilearnbe.modules.work.mapper;

import com.sba.lexilearnbe.modules.work.dto.response.WorkCommentaryResponse;
import com.sba.lexilearnbe.modules.work.entity.WorkCommentary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WorkCommentaryMapper {

    @Mapping(target = "workId", source = "work.id")
    WorkCommentaryResponse toResponse(WorkCommentary commentary);
}
