package com.sba.lexilearnbe.modules.reading.mapper;

import com.sba.lexilearnbe.modules.reading.dto.response.BookmarkResponse;
import com.sba.lexilearnbe.modules.reading.dto.response.BookmarkSectionResponse;
import com.sba.lexilearnbe.modules.reading.dto.response.BookmarkWorkResponse;
import com.sba.lexilearnbe.modules.reading.entity.Bookmark;
import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.workdetail.entity.WorkSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookmarkMapper {

    @Mapping(target = "isCompleted", source = "completed")
    BookmarkResponse toResponse(Bookmark bookmark);

    @Mapping(target = "authorName", source = "author.name", defaultValue = "Khuyết danh")
    BookmarkWorkResponse toResponse(Work work);

    BookmarkSectionResponse toResponse(WorkSection section);
}
