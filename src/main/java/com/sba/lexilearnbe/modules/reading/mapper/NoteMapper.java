package com.sba.lexilearnbe.modules.reading.mapper;

import com.sba.lexilearnbe.modules.reading.dto.response.NoteResponse;
import com.sba.lexilearnbe.modules.reading.entity.Note;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NoteMapper {

    @Mapping(target = "sectionId", source = "section.id")
    NoteResponse toResponse(Note note);
}
