package com.sba.lexilearnbe.modules.work.repository;

import com.sba.lexilearnbe.modules.work.entity.WorkSection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkSectionRepository extends JpaRepository<WorkSection, UUID> {

    @EntityGraph(attributePaths = "work")
    List<WorkSection> findAllByWork_IdOrderByNumberAsc(UUID workId);

    @EntityGraph(attributePaths = "work")
    @Query("SELECT s FROM WorkSection s WHERE s.id = :id")
    Optional<WorkSection> findByIdWithWork(@Param("id") UUID id);

    @Query("SELECT COALESCE(MAX(s.number), 0) FROM WorkSection s WHERE s.work.id = :workId")
    Integer findMaxNumberByWorkId(@Param("workId") UUID workId);

    boolean existsByWork_IdAndNumber(UUID workId, Integer number);

    boolean existsByWork_IdAndNumberAndIdNot(UUID workId, Integer number, UUID sectionId);
    @Modifying
    @Query("DELETE FROM WorkSection ws WHERE ws.work.id = :workId")
    void deleteByWorkId(@Param("workId") UUID workId);
}
