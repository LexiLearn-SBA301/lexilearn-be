package com.sba.lexilearnbe.modules.reading.repository;

import com.sba.lexilearnbe.modules.reading.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NoteRepository extends JpaRepository<Note, UUID> {

    @Query("""
            SELECT n
            FROM Note n
            JOIN FETCH n.section s
            WHERE n.account.id = :accountId
              AND s.id = :sectionId
            ORDER BY n.startOffset ASC
            """)
    List<Note> findByAccountIdAndSectionId(
            @Param("accountId") UUID accountId,
            @Param("sectionId") UUID sectionId
    );

    @Query("""
            SELECT n
            FROM Note n
            JOIN FETCH n.section
            WHERE n.account.id = :accountId
              AND n.id = :noteId
            """)
    Optional<Note> findByAccountIdAndId(
            @Param("accountId") UUID accountId,
            @Param("noteId") UUID noteId
    );
}
