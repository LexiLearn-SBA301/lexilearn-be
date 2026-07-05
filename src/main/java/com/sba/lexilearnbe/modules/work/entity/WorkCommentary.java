package com.sba.lexilearnbe.modules.work.entity;

import com.sba.lexilearnbe.modules.work.enums.CommentatorType;
import com.sba.lexilearnbe.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "work_commentaries",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_work_commentaries_work_display_order",
                columnNames = {"work_id", "display_order"}
        )
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WorkCommentary extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @Column(length = 300)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "commentator_name", nullable = false, length = 200)
    private String commentatorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "commentator_type", nullable = false, length = 30)
    private CommentatorType commentatorType;

    @Column(name = "source_title", length = 300)
    private String sourceTitle;

    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    @Column(name = "published_year")
    private Integer publishedYear;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Builder.Default
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Builder.Default
    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = true;
}
