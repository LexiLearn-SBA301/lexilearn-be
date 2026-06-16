package com.sba.lexilearnbe.modules.work.mapper;

import com.sba.lexilearnbe.modules.work.dto.response.TagResponse;
import com.sba.lexilearnbe.modules.work.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {
    TagResponse toResponse(Tag tag);
}