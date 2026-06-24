package com.sba.lexilearnbe.modules.work.entity;

import com.sba.lexilearnbe.modules.work.enums.WorkSectionContentType;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(
        name = "work_sections",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_work_sections_work_number",
                columnNames = {"work_id", "number"}
        )
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WorkSection extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_id", nullable = false)
    private Work work;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 30)
    private WorkSectionContentType contentType;

    @Column(name = "word_count")
    private Integer wordCount;
}
