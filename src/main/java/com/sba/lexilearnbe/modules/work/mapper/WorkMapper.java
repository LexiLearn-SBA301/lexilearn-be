package com.sba.lexilearnbe.modules.work.mapper;

import com.sba.lexilearnbe.modules.work.dto.request.WorkRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSummaryResponse;
import com.sba.lexilearnbe.modules.work.entity.Tag;
import com.sba.lexilearnbe.modules.work.entity.Work;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkMapper {

    @Mapping(target = "authorName", source = "work.author.name", defaultValue = "Khuyết danh")
    @Mapping(target = "tags", expression = "java(mapTags(tags))")
    WorkSummaryResponse toSummaryResponse(Work work, Set<Tag> tags);

    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "authorName", source = "author.name", defaultValue = "Khuyết danh")
    @Mapping(target = "authorSlug", source = "author.slug")
    @Mapping(target = "tags", expression = "java(mapTags(work.getTags()))")
    WorkDetailResponse toDetailResponse(Work work);

    default List<String> mapTags(Set<Tag> tags) {
        if (tags == null) return Collections.emptyList();
        return tags.stream().map(Tag::getName).toList();
    }
    @Mapping(target = "genre", source = "genre")
    @Mapping(target = "period", source = "period")
    Work toEntity(WorkRequest request);

    @Mapping(target = "genre", source = "genre")
    @Mapping(target = "period", source = "period")
    void updateEntityFromRequest(WorkRequest request, @MappingTarget Work work);
}