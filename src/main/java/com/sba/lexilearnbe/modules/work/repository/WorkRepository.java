package com.sba.lexilearnbe.modules.work.repository;

import com.sba.lexilearnbe.modules.work.entity.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkRepository extends JpaRepository<Work, UUID> {

    Optional<Work> findBySlug(String slug);
    boolean existsBySlug(String slug);

    @Query(value = "SELECT w.* FROM works w LEFT JOIN authors a ON w.author_id = a.id " +
            "WHERE w.is_published = true " +
            "AND (:genre IS NULL OR w.genre = :genre) " +
            "AND (:period IS NULL OR w.period = :period) " +
            "AND (:search IS NULL OR LOWER(w.title::text) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(a.name::text) LIKE LOWER(CONCAT('%', :search, '%')))",
            countQuery = "SELECT count(*) FROM works w LEFT JOIN authors a ON w.author_id = a.id " +
                    "WHERE w.is_published = true " +
                    "AND (:genre IS NULL OR w.genre = :genre) " +
                    "AND (:period IS NULL OR w.period = :period) " +
                    "AND (:search IS NULL OR LOWER(w.title::text) LIKE LOWER(CONCAT('%', :search, '%')) " +
                    "OR LOWER(a.name::text) LIKE LOWER(CONCAT('%', :search, '%')))",
            nativeQuery = true)
    Page<Work> findWorksWithFilter(
            @Param("genre") String genre,
            @Param("period") String period,
            @Param("search") String search,
            Pageable pageable
    );
}