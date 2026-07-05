package com.sba.lexilearnbe.modules.work.repository;

import com.sba.lexilearnbe.modules.work.entity.WorkCommentary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkCommentaryRepository extends JpaRepository<WorkCommentary, UUID> {

    @Query(
            value = """
                    SELECT c
                    FROM WorkCommentary c
                    JOIN FETCH c.work w
                    WHERE w.id = :workId
                      AND c.isPublished = true
                    """,
            countQuery = """
                    SELECT COUNT(c)
                    FROM WorkCommentary c
                    WHERE c.work.id = :workId
                      AND c.isPublished = true
                    """
    )
    Page<WorkCommentary> findPublishedByWorkId(
            @Param("workId") UUID workId,
            Pageable pageable
    );

    @Query(
            value = """
                    SELECT c
                    FROM WorkCommentary c
                    JOIN FETCH c.work w
                    WHERE w.id = :workId
                    """,
            countQuery = """
                    SELECT COUNT(c)
                    FROM WorkCommentary c
                    WHERE c.work.id = :workId
                    """
    )
    Page<WorkCommentary> findAllByWorkId(
            @Param("workId") UUID workId,
            Pageable pageable
    );

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
