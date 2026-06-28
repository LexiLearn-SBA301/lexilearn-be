package com.sba.lexilearnbe.modules.work.mapper;

import com.sba.lexilearnbe.modules.work.dto.request.AuthorRequest;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorSummaryResponse;
import com.sba.lexilearnbe.modules.work.entity.Author;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AuthorMapper {

    AuthorSummaryResponse toSummaryResponse(Author author);

    AuthorDetailResponse toDetailResponse(Author author);

    @Mapping(target = "portraitUrl", ignore = true)
    @Mapping(target = "portraitPublicId", ignore = true)
    Author toEntity(AuthorRequest request);

    @Mapping(target = "portraitUrl", ignore = true)
    @Mapping(target = "portraitPublicId", ignore = true)
    void updateEntityFromRequest(AuthorRequest request, @MappingTarget Author author);
}
