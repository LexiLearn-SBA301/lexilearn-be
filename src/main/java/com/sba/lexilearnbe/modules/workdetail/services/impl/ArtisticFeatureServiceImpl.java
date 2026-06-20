package com.sba.lexilearnbe.modules.workdetail.services.impl;

import com.sba.lexilearnbe.modules.work.entity.Work;
import com.sba.lexilearnbe.modules.work.repository.WorkRepository;
import com.sba.lexilearnbe.modules.workdetail.dto.request.CreateArtisticFeatureRequest;
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

import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
        Work work = requireWork(workId);

        ArtisticFeature feature = ArtisticFeature.builder()
                .work(work)
                .featureType(request.getFeatureType())
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .displayOrder(getNextDisplayOrder(workId))
                .build();

        return artisticFeatureMapper.toResponse(artisticFeatureRepository.save(feature));
    }

    @Override
    @Transactional
    public ArtisticFeatureResponse updateArtisticFeature(UUID featureId, UpdateArtisticFeatureRequest request) {
        ArtisticFeature feature = requireFeature(featureId);

        if (request.getFeatureType() != null) {
            feature.setFeatureType(request.getFeatureType());
        }
        if (request.getTitle() != null) {
            feature.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            feature.setDescription(request.getDescription());
        }

        return artisticFeatureMapper.toResponse(artisticFeatureRepository.save(feature));
    }

    @Override
    @Transactional
    public void deleteArtisticFeature(UUID featureId) {
        artisticFeatureRepository.delete(requireFeature(featureId));
    }

    private Work requireWork(UUID workId) {
        Objects.requireNonNull(workId, "workId không được để trống");

        return workRepository.findById(workId)
                .orElseThrow(() -> new ApiException(ErrorCode.WORK_NOT_FOUND));
    }

    private Work requireReadableWork(UUID workId) {
        Work work = requireWork(workId);
        WorkReadAccessValidator.validate(work);
        return work;
    }

    private ArtisticFeature requireFeature(UUID featureId) {
        Objects.requireNonNull(featureId, "featureId không được để trống");

        return artisticFeatureRepository.findById(featureId)
                .orElseThrow(() -> new ApiException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        "Đặc điểm nghệ thuật không tồn tại"
                ));
    }

    private int getNextDisplayOrder(UUID workId) {
        return artisticFeatureRepository.findMaxDisplayOrderByWorkId(workId) + 1;
    }
}
