package com.sba.lexilearnbe.modules.work.mapper;

import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkCommentaryResponse;
import com.sba.lexilearnbe.modules.work.entity.WorkCommentary;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface WorkCommentaryMapper {

    @Mapping(target = "workId", source = "work.id")
    WorkCommentaryResponse toResponse(WorkCommentary commentary);

    @Mapping(target = "work", ignore = true)
    @Mapping(target = "displayOrder", ignore = true)
    @Mapping(target = "title", source = "title", qualifiedByName = "trimToNull")
    @Mapping(target = "content", source = "content", qualifiedByName = "trim")
    @Mapping(target = "commentatorName", source = "commentatorName", qualifiedByName = "trim")
    @Mapping(target = "sourceTitle", source = "sourceTitle", qualifiedByName = "trimToNull")
    @Mapping(target = "sourceUrl", source = "sourceUrl", qualifiedByName = "trimToNull")
    @Mapping(target = "isFeatured", source = "isFeatured", defaultValue = "false")
    @Mapping(target = "isPublished", source = "isPublished", defaultValue = "true")
    WorkCommentary toEntity(CreateWorkCommentaryRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "work", ignore = true)
    @Mapping(target = "displayOrder", ignore = true)
    @Mapping(target = "title", source = "title", qualifiedByName = "trimToNull")
    @Mapping(target = "content", source = "content", qualifiedByName = "trim")
    @Mapping(target = "commentatorName", source = "commentatorName", qualifiedByName = "trim")
    @Mapping(target = "sourceTitle", source = "sourceTitle", qualifiedByName = "trimToNull")
    @Mapping(target = "sourceUrl", source = "sourceUrl", qualifiedByName = "trimToNull")
    void updateEntityFromRequest(
            UpdateWorkCommentaryRequest request,
            @MappingTarget WorkCommentary commentary
    );

    @Named("trim")
    default String trim(String value) {
        return value == null ? null : value.trim();
    }

    @Named("trimToNull")
    default String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
