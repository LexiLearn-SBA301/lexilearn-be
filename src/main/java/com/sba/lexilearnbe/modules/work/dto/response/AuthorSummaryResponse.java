package com.sba.lexilearnbe.modules.work.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorSummaryResponse {
    private UUID id;
    private String name;
    private String slug;
    private String portraitUrl;
    private String period;
    private String bio;
    private Integer birthYear;
    private Integer deathYear;
}