package com.sba.lexilearnbe.modules.work.entity;

import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WorkTagId implements Serializable {
    private UUID workId;
    private UUID tagId;
}