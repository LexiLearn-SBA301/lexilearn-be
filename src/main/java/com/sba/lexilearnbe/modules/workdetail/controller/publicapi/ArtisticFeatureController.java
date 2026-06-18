package com.sba.lexilearnbe.modules.workdetail.controller.publicapi;

import com.sba.lexilearnbe.modules.workdetail.dto.response.ArtisticFeatureResponse;
import com.sba.lexilearnbe.modules.workdetail.services.ArtisticFeatureService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Artistic Feature", description = "API đọc đặc điểm nghệ thuật")
public class ArtisticFeatureController {

    private final ArtisticFeatureService artisticFeatureService;

    @GetMapping("/works/{workId}/artistic-features")
    @Operation(summary = "Lấy danh sách đặc điểm nghệ thuật của tác phẩm")
    public ResponseEntity<ApiResponse<List<ArtisticFeatureResponse>>> getArtisticFeatures(
            @PathVariable UUID workId
    ) {
        List<ArtisticFeatureResponse> result = artisticFeatureService.getArtisticFeatures(workId);

        ApiResponse<List<ArtisticFeatureResponse>> response =
                ApiResponse.<List<ArtisticFeatureResponse>>builder()
                        .message("Lấy danh sách đặc điểm nghệ thuật thành công")
                        .result(result)
                        .build();

        return ResponseEntity.ok(response);
    }
}
