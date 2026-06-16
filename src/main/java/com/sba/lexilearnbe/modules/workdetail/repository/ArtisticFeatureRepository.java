package com.sba.lexilearnbe.modules.workdetail.repository;

import com.sba.lexilearnbe.modules.workdetail.entity.ArtisticFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ArtisticFeatureRepository extends JpaRepository<ArtisticFeature, UUID> {

    List<ArtisticFeature> findAllByWork_IdOrderByDisplayOrderAsc(UUID workId);
}
