package com.sba.lexilearnbe.modules.workdetail.repository;

import com.sba.lexilearnbe.modules.workdetail.entity.ArtisticFeature;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ArtisticFeatureRepository extends JpaRepository<ArtisticFeature, UUID> {

    @EntityGraph(attributePaths = "work")
    List<ArtisticFeature> findAllByWork_IdOrderByDisplayOrderAsc(UUID workId);

    @EntityGraph(attributePaths = "work")
    @Query("SELECT f FROM ArtisticFeature f WHERE f.id = :id")
    Optional<ArtisticFeature> findByIdWithWork(@Param("id") UUID id);

    @Query("SELECT COALESCE(MAX(f.displayOrder), -1) FROM ArtisticFeature f WHERE f.work.id = :workId")
    Integer findMaxDisplayOrderByWorkId(@Param("workId") UUID workId);
}
