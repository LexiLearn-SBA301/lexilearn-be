package com.sba.lexilearnbe.modules.auth.repository;

import com.sba.lexilearnbe.modules.auth.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    // email đã được lowercase ở tầng service trước khi gọi
    boolean existsByEmail(String email);

    Optional<Account> findByEmail(String email);
}
