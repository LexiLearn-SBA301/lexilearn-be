package com.sba.lexilearnbe.modules.workdetail.repository;

import com.sba.lexilearnbe.modules.workdetail.entity.WorkCharacter;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkCharacterRepository extends JpaRepository<WorkCharacter, UUID> {

    @Override
    @EntityGraph(attributePaths = "work")
    Optional<WorkCharacter> findById(UUID id);

    @EntityGraph(attributePaths = "work")
    List<WorkCharacter> findAllByWork_IdOrderByDisplayOrderAsc(UUID workId);

    @Query("SELECT COALESCE(MAX(c.displayOrder), -1) FROM WorkCharacter c WHERE c.work.id = :workId")
    Integer findMaxDisplayOrderByWorkId(@Param("workId") UUID workId);
}
