package com.sba.lexilearnbe.modules.auth.entity;

import com.sba.lexilearnbe.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "roles")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Role extends BaseEntity {

    // Không có prefix ROLE_ trong DB; prefix đó thêm ở tầng Spring Security
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;
}
