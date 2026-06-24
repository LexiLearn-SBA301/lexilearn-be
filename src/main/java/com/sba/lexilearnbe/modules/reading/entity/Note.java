package com.sba.lexilearnbe.modules.reading.entity;

import com.sba.lexilearnbe.modules.auth.entity.Account;
import com.sba.lexilearnbe.modules.reading.enums.NoteColor;
import com.sba.lexilearnbe.modules.workdetail.entity.WorkSection;
import com.sba.lexilearnbe.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "notes")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Note extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false)
    private WorkSection section;

    @Column(name = "start_offset", nullable = false)
    private Integer startOffset;

    @Column(name = "end_offset", nullable = false)
    private Integer endOffset;

    @Column(name = "highlighted_text", nullable = false, columnDefinition = "TEXT")
    private String highlightedText;

    @Column(name = "user_note", columnDefinition = "TEXT")
    private String userNote;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NoteColor color;
}
