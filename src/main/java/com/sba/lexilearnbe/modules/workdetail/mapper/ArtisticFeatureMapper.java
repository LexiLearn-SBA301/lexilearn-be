package com.sba.lexilearnbe.modules.workdetail.mapper;

import com.sba.lexilearnbe.modules.workdetail.dto.response.ArtisticFeatureResponse;
import com.sba.lexilearnbe.modules.workdetail.entity.ArtisticFeature;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArtisticFeatureMapper {

    @Mapping(target = "workId", source = "work.id")
    ArtisticFeatureResponse toResponse(ArtisticFeature feature);
}
