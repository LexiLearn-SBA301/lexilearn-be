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

    @EntityGraph(attributePaths = {"author", "tags"})
    @Query("SELECT w FROM Work w WHERE w.id = :id")
    Optional<Work> findByIdWithAuthorAndTags(@Param("id") UUID id);

    boolean existsBySlug(String slug);
    @Override
    @EntityGraph(attributePaths = {"author"})
    Page<Work> findAll(Specification<Work> spec, Pageable pageable);
    @EntityGraph(attributePaths = {"author", "tags"})
    List<Work> findAllByIdIn(List<UUID> ids);

    boolean existsByAuthorId(UUID id);

    @Query("SELECT w.id FROM Work w WHERE w.author.id = :authorId")
    List<UUID> findIdsByAuthorId(@Param("authorId") UUID authorId);

    @EntityGraph(attributePaths = {"author", "tags"})
    @Query("SELECT w FROM Work w WHERE w.slug = :slug AND w.isPublished = true")
    Optional<Work> findPublishedBySlug(@Param("slug") String slug);
}
