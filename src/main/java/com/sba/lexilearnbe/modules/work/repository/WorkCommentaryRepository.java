package com.sba.lexilearnbe.modules.work.repository;

import com.sba.lexilearnbe.modules.work.entity.WorkCommentary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkCommentaryRepository extends JpaRepository<WorkCommentary, UUID> {

    @Query("""
            SELECT c
            FROM WorkCommentary c
            JOIN FETCH c.work w
            WHERE w.id = :workId
              AND c.isPublished = true
            ORDER BY c.displayOrder ASC
            """)
    List<WorkCommentary> findPublishedByWorkId(@Param("workId") UUID workId);

    @Query("""
            SELECT c
            FROM WorkCommentary c
            JOIN FETCH c.work w
            WHERE w.id = :workId
            ORDER BY c.displayOrder ASC
            """)
    List<WorkCommentary> findAllByWorkId(@Param("workId") UUID workId);

    @Query("""
            SELECT c
            FROM WorkCommentary c
            JOIN FETCH c.work
            WHERE c.id = :commentaryId
            """)
    Optional<WorkCommentary> findByIdWithWork(@Param("commentaryId") UUID commentaryId);

    @Query("""
            SELECT COALESCE(MAX(c.displayOrder), -1)
            FROM WorkCommentary c
            WHERE c.work.id = :workId
            """)
    Integer findMaxDisplayOrderByWorkId(@Param("workId") UUID workId);
}
