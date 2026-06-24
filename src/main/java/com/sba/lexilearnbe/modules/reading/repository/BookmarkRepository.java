package com.sba.lexilearnbe.modules.reading.repository;

import com.sba.lexilearnbe.modules.reading.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {

    @Query("""
            SELECT b
            FROM Bookmark b
            JOIN FETCH b.work w
            LEFT JOIN FETCH w.author
            LEFT JOIN FETCH b.currentSection
            WHERE b.account.id = :accountId
              AND w.isPublished = true
            ORDER BY b.updatedAt DESC
            """)
    List<Bookmark> findReadableBookmarksByAccountId(@Param("accountId") UUID accountId);

    @Query("""
            SELECT b
            FROM Bookmark b
            JOIN FETCH b.work w
            LEFT JOIN FETCH w.author
            LEFT JOIN FETCH b.currentSection
            WHERE b.account.id = :accountId
              AND w.id = :workId
            """)
    Optional<Bookmark> findByAccountIdAndWorkId(
            @Param("accountId") UUID accountId,
            @Param("workId") UUID workId
    );

    @Query("""
            SELECT b
            FROM Bookmark b
            WHERE b.account.id = :accountId
              AND b.id = :bookmarkId
            """)
    Optional<Bookmark> findByAccountIdAndId(
            @Param("accountId") UUID accountId,
            @Param("bookmarkId") UUID bookmarkId
    );
}
