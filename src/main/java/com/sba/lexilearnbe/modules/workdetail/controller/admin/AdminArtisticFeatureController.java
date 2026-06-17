package com.sba.lexilearnbe.modules.workdetail.controller.admin;

import com.sba.lexilearnbe.modules.workdetail.dto.request.CreateArtisticFeatureRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.request.ReorderArtisticFeaturesRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.request.UpdateArtisticFeatureRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.response.ArtisticFeatureResponse;
import com.sba.lexilearnbe.modules.workdetail.services.ArtisticFeatureService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Artistic Feature", description = "API quản trị đặc điểm nghệ thuật")
public class AdminArtisticFeatureController {

    private final ArtisticFeatureService artisticFeatureService;

    @PostMapping("/works/{workId}/artistic-features")
    @Operation(summary = "Tạo đặc điểm nghệ thuật")
    public ResponseEntity<ApiResponse<ArtisticFeatureResponse>> createArtisticFeature(
            @PathVariable UUID workId,
            @Valid @RequestBody CreateArtisticFeatureRequest request
    ) {
        ArtisticFeatureResponse result = artisticFeatureService.createArtisticFeature(workId, request);

        ApiResponse<ArtisticFeatureResponse> response =
                ApiResponse.<ArtisticFeatureResponse>builder()
                        .message("Tạo đặc điểm nghệ thuật thành công")
                        .result(result)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/artistic-features/{featureId}")
    @Operation(summary = "Cập nhật đặc điểm nghệ thuật")
    public ResponseEntity<ApiResponse<ArtisticFeatureResponse>> updateArtisticFeature(
            @PathVariable UUID featureId,
            @Valid @RequestBody UpdateArtisticFeatureRequest request
    ) {
        ArtisticFeatureResponse result = artisticFeatureService.updateArtisticFeature(featureId, request);

        ApiResponse<ArtisticFeatureResponse> response =
                ApiResponse.<ArtisticFeatureResponse>builder()
                        .message("Cập nhật đặc điểm nghệ thuật thành công")
                        .result(result)
                        .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/artistic-features/{featureId}")
    @Operation(summary = "Xóa đặc điểm nghệ thuật")
    public ResponseEntity<ApiResponse<Void>> deleteArtisticFeature(
            @PathVariable UUID featureId
    ) {
        artisticFeatureService.deleteArtisticFeature(featureId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .message("Xóa đặc điểm nghệ thuật thành công")
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/works/{workId}/artistic-features/sequence")
    @Operation(summary = "Sắp xếp lại thứ tự đặc điểm nghệ thuật")
    public ResponseEntity<ApiResponse<List<ArtisticFeatureResponse>>> reorderArtisticFeatures(
            @PathVariable UUID workId,
            @Valid @RequestBody ReorderArtisticFeaturesRequest request
    ) {
        List<ArtisticFeatureResponse> result =
                artisticFeatureService.reorderArtisticFeatures(workId, request);

        ApiResponse<List<ArtisticFeatureResponse>> response =
                ApiResponse.<List<ArtisticFeatureResponse>>builder()
                        .message("Sắp xếp thứ tự đặc điểm nghệ thuật thành công")
                        .result(result)
                        .build();

        return ResponseEntity.ok(response);
    }
}
