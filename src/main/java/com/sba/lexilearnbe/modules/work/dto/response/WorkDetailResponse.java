package com.sba.lexilearnbe.modules.work.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkDetailResponse {
    private UUID id;
    private String title;
    private String originalTitle;
    private String slug;

    // Thông tin Tác giả
    private UUID authorId;
    private String authorName;
    private String authorSlug;

    // Phân loại
    private String genre;
    private String subGenre;
    private String period;
    private Integer grade;
    private Integer semester;
    private Integer publishYear;

    // Tổng quan
    private String summary;
    private String coverUrl;
    private Long viewCount;

    // Metadata phân tích
    private String historicalContext;
    private String realisticValue;
    private String humanisticValue;
    private String artisticValue;
    private String famousQuote;
    private String quoteAttribution;

    // Tags
    private List<String> tags;

    private LocalDateTime updatedAt;
}