package com.sba.lexilearnbe.modules.work.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "work_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(WorkTagId.class) // Sử dụng composite key
public class WorkTag {
    @Id
    @Column(name = "work_id")
    private UUID workId;

    @Id
    @Column(name = "tag_id")
    private UUID tagId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}