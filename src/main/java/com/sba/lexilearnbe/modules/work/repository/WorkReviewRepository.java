package com.sba.lexilearnbe.modules.work.repository;

import com.sba.lexilearnbe.modules.work.entity.WorkReview;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkReviewRepository extends JpaRepository<WorkReview, UUID> {

    @Query(
            value = """
                    SELECT r
                    FROM WorkReview r
                    JOIN FETCH r.work
                    JOIN FETCH r.account
                    WHERE r.account.id = :accountId
                    """,
            countQuery = """
                    SELECT COUNT(r)
                    FROM WorkReview r
                    WHERE r.account.id = :accountId
                    """
    )
    Page<WorkReview> findAllByAccountId(
            @Param("accountId") UUID accountId,
            Pageable pageable
    );

    @Query("""
            SELECT r
            FROM WorkReview r
            JOIN FETCH r.work
            JOIN FETCH r.account
            WHERE r.id = :reviewId
              AND r.account.id = :accountId
            """)
    Optional<WorkReview> findOwnedById(
            @Param("reviewId") UUID reviewId,
            @Param("accountId") UUID accountId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT r
            FROM WorkReview r
            JOIN FETCH r.work
            JOIN FETCH r.account
            WHERE r.id = :reviewId
              AND r.account.id = :accountId
            """)
    Optional<WorkReview> findOwnedByIdForUpdate(
            @Param("reviewId") UUID reviewId,
            @Param("accountId") UUID accountId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT r
            FROM WorkReview r
            JOIN FETCH r.work
            JOIN FETCH r.account
            WHERE r.id = :reviewId
            """)
    Optional<WorkReview> findByIdForUpdate(@Param("reviewId") UUID reviewId);
}
