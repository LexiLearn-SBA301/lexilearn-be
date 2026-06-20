package com.sba.lexilearnbe.modules.workdetail.repository;

import com.sba.lexilearnbe.modules.workdetail.entity.WorkCharacter;
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
    @Query("SELECT c FROM WorkCharacter c JOIN FETCH c.work WHERE c.id = :id")
    Optional<WorkCharacter> findById(UUID id);

    @Query("SELECT c FROM WorkCharacter c JOIN FETCH c.work WHERE c.work.id = :workId ORDER BY c.displayOrder ASC")
    List<WorkCharacter> findAllByWork_IdOrderByDisplayOrderAsc(UUID workId);

    @Query("SELECT COALESCE(MAX(c.displayOrder), -1) FROM WorkCharacter c WHERE c.work.id = :workId")
    Integer findMaxDisplayOrderByWorkId(@Param("workId") UUID workId);
}
