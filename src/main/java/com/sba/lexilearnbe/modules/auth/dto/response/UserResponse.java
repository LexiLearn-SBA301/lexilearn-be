package com.sba.lexilearnbe.modules.auth.dto.response;

import com.sba.lexilearnbe.modules.auth.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private UUID id;

    private String fullName;

    private String email;

    private AccountStatus status;

    /** Danh sách tên role (không có prefix ROLE_) — FE dùng để phân quyền admin/user. */
    private Set<String> roles;

    private LocalDateTime emailVerifiedAt;

    private LocalDateTime createdAt;
}
