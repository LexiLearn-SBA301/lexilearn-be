package com.sba.lexilearnbe.modules.work.repository;

import com.sba.lexilearnbe.modules.work.entity.WorkReviewRevision;
import com.sba.lexilearnbe.modules.work.enums.ReviewRevisionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkReviewRevisionRepository
        extends JpaRepository<WorkReviewRevision, UUID> {

    @Query(
            value = """
                    SELECT v
                    FROM WorkReviewRevision v
                    JOIN FETCH v.review r
                    JOIN FETCH r.work w
                    JOIN FETCH r.account
                    LEFT JOIN FETCH v.reviewedBy
                    WHERE w.id = :workId
                      AND v.status = :status
                    """,
            countQuery = """
                    SELECT COUNT(v)
                    FROM WorkReviewRevision v
                    WHERE v.review.work.id = :workId
                      AND v.status = :status
                    """
    )
    Page<WorkReviewRevision> findPublicByWorkId(
            @Param("workId") UUID workId,
            @Param("status") ReviewRevisionStatus status,
            Pageable pageable
    );

    @Query("""
            SELECT v
            FROM WorkReviewRevision v
            LEFT JOIN FETCH v.reviewedBy
            WHERE v.review.id = :reviewId
              AND v.status = :status
            """)
    Optional<WorkReviewRevision> findByReviewIdAndStatus(
            @Param("reviewId") UUID reviewId,
            @Param("status") ReviewRevisionStatus status
    );

    @Query("""
            SELECT v
            FROM WorkReviewRevision v
            LEFT JOIN FETCH v.reviewedBy
            WHERE v.review.id = :reviewId
              AND v.versionNumber = (
                  SELECT MAX(latest.versionNumber)
                  FROM WorkReviewRevision latest
                  WHERE latest.review.id = :reviewId
              )
            """)
    Optional<WorkReviewRevision> findLatestByReviewId(@Param("reviewId") UUID reviewId);

    @Query("""
            SELECT COALESCE(MAX(v.versionNumber), 0)
            FROM WorkReviewRevision v
            WHERE v.review.id = :reviewId
            """)
    Integer findMaxVersionByReviewId(@Param("reviewId") UUID reviewId);

    @Query("""
            SELECT v
            FROM WorkReviewRevision v
            JOIN FETCH v.review
            LEFT JOIN FETCH v.reviewedBy
            WHERE v.review.id IN :reviewIds
              AND (
                  v.status IN :activeStatuses
                  OR (
                      v.status = :rejectedStatus
                      AND v.versionNumber = (
                          SELECT MAX(rejected.versionNumber)
                          FROM WorkReviewRevision rejected
                          WHERE rejected.review.id = v.review.id
                            AND rejected.status = :rejectedStatus
                      )
                  )
              )
            """)
    List<WorkReviewRevision> findCurrentStatesByReviewIds(
            @Param("reviewIds") Collection<UUID> reviewIds,
            @Param("activeStatuses") Collection<ReviewRevisionStatus> activeStatuses,
            @Param("rejectedStatus") ReviewRevisionStatus rejectedStatus
    );
}
