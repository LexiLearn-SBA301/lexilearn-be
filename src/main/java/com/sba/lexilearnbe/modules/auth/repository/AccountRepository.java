package com.sba.lexilearnbe.modules.auth.repository;

import com.sba.lexilearnbe.modules.auth.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    // email đã được lowercase ở tầng service trước khi gọi
    Optional<Account> findByEmail(String email);

    /**
     * Fetch kèm roles (JOIN FETCH) — dùng cho path cần sinh JWT (login).
     * roles là LAZY nên BẮT BUỘC dùng method này khi sẽ đọc account.getRoles()
     * ngoài transaction, tránh LazyInitializationException.
     */
    @EntityGraph(attributePaths = "roles")
    Optional<Account> findWithRolesByEmail(String email);

    /** Như {@link #findWithRolesByEmail} nhưng theo id — dùng cho path refresh token. */
    @EntityGraph(attributePaths = "roles")
    Optional<Account> findWithRolesById(UUID id);
}
