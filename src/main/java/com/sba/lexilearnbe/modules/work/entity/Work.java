package com.sba.lexilearnbe.modules.work.entity;

import com.sba.lexilearnbe.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "works")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Work extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private Author author;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, unique = true, length = 300)
    private String slug;

    @Column(name = "original_title", length = 300)
    private String originalTitle;

    @Column(nullable = false, length = 50)
    private String genre;

    @Column(name = "sub_genre", length = 50)
    private String subGenre;

    @Column(nullable = false, length = 30)
    private String period;

    private Integer grade;
    private Integer semester;

    @Column(name = "publish_year")
    private Integer publishYear;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "cover_url", length = 500)
    private String coverUrl;

    @Column(name = "cover_public_id", length = 500)
    private String coverPublicId;

    @Builder.Default
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = false;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "historical_context", columnDefinition = "TEXT")
    private String historicalContext;

    @Column(name = "realistic_value", columnDefinition = "TEXT")
    private String realisticValue;

    @Column(name = "humanistic_value", columnDefinition = "TEXT")
    private String humanisticValue;

    @Column(name = "artistic_value", columnDefinition = "TEXT")
    private String artisticValue;

    @Column(name = "famous_quote", columnDefinition = "TEXT")
    private String famousQuote;

    @Column(name = "quote_attribution", length = 300)
    private String quoteAttribution;

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "work_tags",
            joinColumns = @JoinColumn(name = "work_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
}
