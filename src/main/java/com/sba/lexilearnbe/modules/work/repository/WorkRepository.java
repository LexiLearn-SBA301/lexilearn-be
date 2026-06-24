package com.sba.lexilearnbe.modules.work.repository;

import com.sba.lexilearnbe.modules.work.entity.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkRepository extends JpaRepository<Work, UUID>, JpaSpecificationExecutor<Work> {

    @EntityGraph(attributePaths = {"author", "tags"})
    Optional<Work> findBySlug(String slug);
    boolean existsBySlug(String slug);
    @Override
    @EntityGraph(attributePaths = {"author"})
    Page<Work> findAll(Specification<Work> spec, Pageable pageable);
    @Query("SELECT DISTINCT w FROM Work w LEFT JOIN FETCH w.tags WHERE w IN :works")
    List<Work> fetchTagsForWorks(@Param("works") List<Work> works);

    boolean existsByAuthorId(UUID id);
    @EntityGraph(attributePaths = {"author", "tags"})
    @Query("SELECT w FROM Work w WHERE w.slug = :slug AND w.isPublished = true")
    Optional<Work> findPublishedBySlug(@Param("slug") String slug);
}