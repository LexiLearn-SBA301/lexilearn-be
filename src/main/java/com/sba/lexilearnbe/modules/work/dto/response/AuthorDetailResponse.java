package com.sba.lexilearnbe.modules.work.dto.response;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthorDetailResponse {
    private UUID id;
    private String name;
    private String penName;
    private String slug;
    private Integer birthYear;
    private Integer deathYear;
    private String period;
    private String bio;
    private String portraitUrl;
}