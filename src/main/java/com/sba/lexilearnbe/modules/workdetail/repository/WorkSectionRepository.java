package com.sba.lexilearnbe.modules.workdetail.repository;

import com.sba.lexilearnbe.modules.workdetail.entity.WorkSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkSectionRepository extends JpaRepository<WorkSection, UUID> {

    @Override
    @Query("SELECT s FROM WorkSection s JOIN FETCH s.work WHERE s.id = :id")
    Optional<WorkSection> findById(@Param("id") UUID id);

    @Query("SELECT s FROM WorkSection s JOIN FETCH s.work WHERE s.work.id = :workId ORDER BY s.number ASC")
    List<WorkSection> findAllByWork_IdOrderByNumberAsc(@Param("workId") UUID workId);

    @Query("SELECT COALESCE(MAX(s.number), 0) FROM WorkSection s WHERE s.work.id = :workId")
    Integer findMaxNumberByWorkId(@Param("workId") UUID workId);

    boolean existsByWork_IdAndNumber(UUID workId, Integer number);

    boolean existsByWork_IdAndNumberAndIdNot(UUID workId, Integer number, UUID sectionId);
}
