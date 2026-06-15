package com.sba.lexilearnbe.modules.work.dto.response;

import lombok.*;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkSummaryResponse {
    private UUID id;
    private String slug;
    private String title;
    private String authorName;
    private String coverUrl;
    private String subGenre;
    private String famousQuote;
    private List<String> tags;
}