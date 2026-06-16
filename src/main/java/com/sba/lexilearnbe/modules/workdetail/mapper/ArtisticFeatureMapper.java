package com.sba.lexilearnbe.modules.workdetail.mapper;

import com.sba.lexilearnbe.modules.workdetail.dto.response.ArtisticFeatureResponse;
import com.sba.lexilearnbe.modules.workdetail.entity.ArtisticFeature;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ArtisticFeatureMapper {

    @Mapping(target = "workId", source = "work.id")
    ArtisticFeatureResponse toResponse(ArtisticFeature feature);
}
