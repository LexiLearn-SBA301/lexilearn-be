package com.sba.lexilearnbe.modules.workdetail.services.impl;

import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.workdetail.dto.request.CreateArtisticFeatureRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.request.ReorderArtisticFeaturesRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.request.UpdateArtisticFeatureRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.response.ArtisticFeatureResponse;
import com.sba.lexilearnbe.modules.workdetail.entity.ArtisticFeature;
import com.sba.lexilearnbe.modules.workdetail.mapper.ArtisticFeatureMapper;
import com.sba.lexilearnbe.modules.workdetail.repository.ArtisticFeatureRepository;
import com.sba.lexilearnbe.modules.workdetail.services.ArtisticFeatureService;
import com.sba.lexilearnbe.modules.workdetail.util.WorkReadAccessValidator;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArtisticFeatureServiceImpl implements ArtisticFeatureService {

    private final WorkRepository workRepository;
    private final ArtisticFeatureRepository artisticFeatureRepository;
    private final ArtisticFeatureMapper artisticFeatureMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ArtisticFeatureResponse> getArtisticFeatures(UUID workId) {
        requireReadableWork(workId);

        return artisticFeatureRepository.findAllByWork_IdOrderByDisplayOrderAsc(workId)
                .stream()
                .map(artisticFeatureMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ArtisticFeatureResponse createArtisticFeature(UUID workId, CreateArtisticFeatureRequest request) {
        ArtisticFeature feature = ArtisticFeature.builder()
                .work(requireWork(workId))
                .title(request.getTitle().trim())
                .content(request.getContent())
                .displayOrder(request.getDisplayOrder())
                .build();

        return artisticFeatureMapper.toResponse(artisticFeatureRepository.save(feature));
    }

    @Override
    @Transactional
    public ArtisticFeatureResponse updateArtisticFeature(UUID featureId, UpdateArtisticFeatureRequest request) {
        ArtisticFeature feature = requireFeature(featureId);

        if (request.getTitle() != null) {
            feature.setTitle(request.getTitle().trim());
        }
        if (request.getContent() != null) {
            feature.setContent(request.getContent());
        }
        if (request.getDisplayOrder() != null) {
            feature.setDisplayOrder(request.getDisplayOrder());
        }

        return artisticFeatureMapper.toResponse(artisticFeatureRepository.save(feature));
    }

    @Override
    @Transactional
    public void deleteArtisticFeature(UUID featureId) {
        artisticFeatureRepository.delete(requireFeature(featureId));
    }

    @Override
    @Transactional
    public List<ArtisticFeatureResponse> reorderArtisticFeatures(UUID workId, ReorderArtisticFeaturesRequest request) {
        requireWork(workId);
        List<ArtisticFeature> features =
                artisticFeatureRepository.findAllByWork_IdOrderByDisplayOrderAsc(workId);
        validateReorderIds(
                features.stream().map(ArtisticFeature::getId).collect(Collectors.toSet()),
                request.getFeatureIds()
        );

        Map<UUID, ArtisticFeature> featuresById = new HashMap<>();
        features.forEach(feature -> featuresById.put(feature.getId(), feature));

        for (int index = 0; index < request.getFeatureIds().size(); index++) {
            featuresById.get(request.getFeatureIds().get(index)).setDisplayOrder(index);
        }

        artisticFeatureRepository.saveAll(features);

        return request.getFeatureIds().stream()
                .map(featuresById::get)
                .map(artisticFeatureMapper::toResponse)
                .toList();
    }

    private Work requireWork(UUID workId) {
        return workRepository.findById(workId)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));
    }

    private Work requireReadableWork(UUID workId) {
        Work work = requireWork(workId);
        WorkReadAccessValidator.validate(work);
        return work;
    }

    private ArtisticFeature requireFeature(UUID featureId) {
        return artisticFeatureRepository.findById(featureId)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Đặc điểm nghệ thuật không tồn tại"
                ));
    }

    private void validateReorderIds(Set<UUID> existingIds, List<UUID> requestedIds) {
        Set<UUID> requestedUniqueIds = new HashSet<>(requestedIds);
        if (requestedIds.size() != requestedUniqueIds.size() || !existingIds.equals(requestedUniqueIds)) {
            throw new ApiException(
                    ErrorCode.VALIDATION_ERROR,
                    "Danh sách sắp xếp phải chứa đúng toàn bộ đặc điểm nghệ thuật và không được trùng ID"
            );
        }
    }
}
