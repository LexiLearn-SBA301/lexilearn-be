package com.sba.lexilearnbe.modules.workdetail.mapper;

import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkSectionDetailResponse;
import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkSectionSummaryResponse;
import com.sba.lexilearnbe.modules.workdetail.entity.WorkSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkSectionMapper {

    @Mapping(target = "workId", source = "work.id")
    WorkSectionSummaryResponse toSummary(WorkSection section);

    @Mapping(target = "workId", source = "work.id")
    WorkSectionDetailResponse toDetail(WorkSection section);
}
