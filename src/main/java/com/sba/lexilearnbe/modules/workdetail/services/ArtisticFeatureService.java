package com.sba.lexilearnbe.modules.workdetail.services;

import com.sba.lexilearnbe.modules.workdetail.dto.request.CreateArtisticFeatureRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.request.UpdateArtisticFeatureRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.response.ArtisticFeatureResponse;

import java.util.List;
import java.util.UUID;

public interface ArtisticFeatureService {

    List<ArtisticFeatureResponse> getArtisticFeatures(UUID workId);

    ArtisticFeatureResponse createArtisticFeature(UUID workId, CreateArtisticFeatureRequest request);

    ArtisticFeatureResponse updateArtisticFeature(UUID featureId, UpdateArtisticFeatureRequest request);

    void deleteArtisticFeature(UUID featureId);
}
