package com.sba.lexilearnbe.modules.work.entity;

import com.sba.lexilearnbe.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Author extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "pen_name", length = 200)
    private String penName;

    @Column(nullable = false, unique = true, length = 200)
    private String slug;

    @Column(name = "birth_year")
    private Integer birthYear;

    @Column(name = "death_year")
    private Integer deathYear;

    @Column(nullable = false, length = 30)
    private String period;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "portrait_url", length = 500)
    private String portraitUrl;

    @Column(name = "portrait_public_id", length = 500)
    private String portraitPublicId;
}
