package com.sba.lexilearnbe.modules.work.dto.response;

import com.sba.lexilearnbe.modules.work.enums.WorkSectionContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkSectionSummaryResponse {

    private UUID id;
    private UUID workId;
    private Integer number;
    private String title;
    private WorkSectionContentType contentType;
    private Integer wordCount;
}
