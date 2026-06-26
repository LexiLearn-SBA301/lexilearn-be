package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.request.CreateArtisticFeatureRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateArtisticFeatureRequest;
import com.sba.lexilearnbe.modules.work.dto.response.ArtisticFeatureResponse;

import java.util.List;
import java.util.UUID;

public interface ArtisticFeatureService {

    List<ArtisticFeatureResponse> getArtisticFeatures(UUID workId);

    ArtisticFeatureResponse createArtisticFeature(UUID workId, CreateArtisticFeatureRequest request);

    ArtisticFeatureResponse updateArtisticFeature(UUID workId, UUID featureId, UpdateArtisticFeatureRequest request);

    void deleteArtisticFeature(UUID workId, UUID featureId);
}
