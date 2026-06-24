package com.sba.lexilearnbe.modules.work.repository;

import com.sba.lexilearnbe.modules.work.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TagRepository extends JpaRepository<Tag, UUID> {
    Page<Tag> findByNameContainingIgnoreCase(String name, Pageable pageable);
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
    @Modifying
    @Query(value = "DELETE FROM work_tags WHERE tag_id = :tagId", nativeQuery = true)
    void deleteTagFromAllWorks(@Param("tagId") UUID tagId);
}