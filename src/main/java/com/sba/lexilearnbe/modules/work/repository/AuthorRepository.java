package com.sba.lexilearnbe.modules.work.repository;

import com.sba.lexilearnbe.modules.work.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorRepository extends JpaRepository<Author, UUID> {
    Optional<Author> findBySlug(String slug);

    @Query("SELECT a FROM Author a WHERE " +
            "(:search = '' OR a.name ILIKE CONCAT('%', :search, '%') OR a.penName ILIKE CONCAT('%', :search, '%')) " +
            "AND (:period = '' OR a.period = :period)")
    Page<Author> findAuthorsWithFilter(@Param("search") String search, @Param("period") String period, Pageable pageable);
    boolean existsBySlug(String slug);
}