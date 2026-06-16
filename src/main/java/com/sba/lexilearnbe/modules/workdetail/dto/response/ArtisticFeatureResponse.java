package com.sba.lexilearnbe.modules.workdetail.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtisticFeatureResponse {

    private UUID id;
    private UUID workId;
    private String title;
    private String content;
    private Integer displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
