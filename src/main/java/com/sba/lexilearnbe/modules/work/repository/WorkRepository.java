package com.sba.lexilearnbe.modules.work.repository;

import com.sba.lexilearnbe.modules.work.entity.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkRepository extends JpaRepository<Work, UUID> {

    Optional<Work> findBySlug(String slug);
    boolean existsBySlug(String slug);
    @EntityGraph(attributePaths = {"author"})
    @Query("SELECT DISTINCT w FROM Work w " +
            "LEFT JOIN w.author a " +
            "WHERE w.isPublished = true " +
            "AND (:genre IS NULL OR w.genre = :genre) " +
            "AND (:period IS NULL OR w.period = :period) " +
            "AND (:search IS NULL OR LOWER(w.title) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
            "OR LOWER(a.name) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')))")
    Page<Work> findWorksWithFilter(
            @Param("genre") String genre,
            @Param("period") String period,
            @Param("search") String search,
            Pageable pageable
    );
    @Query("SELECT DISTINCT w FROM Work w LEFT JOIN FETCH w.tags WHERE w IN :works")
    List<Work> fetchTagsForWorks(@Param("works") List<Work> works);
}