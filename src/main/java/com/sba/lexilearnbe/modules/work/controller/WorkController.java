package com.sba.lexilearnbe.modules.work.controller;

import com.sba.lexilearnbe.modules.work.dto.request.*;
import com.sba.lexilearnbe.modules.work.dto.response.*;
import com.sba.lexilearnbe.modules.work.services.ArtisticFeatureService;
import com.sba.lexilearnbe.modules.work.services.WorkCharacterService;
import com.sba.lexilearnbe.modules.work.services.WorkSectionService;
import com.sba.lexilearnbe.modules.work.services.WorkService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/works")
@RequiredArgsConstructor
@Tag(name = "Work", description = "Các API dành cho Thư viện tác phẩm")
public class WorkController {

    private final WorkService workService;
    private final WorkSectionService workSectionService;
    private final WorkCharacterService workCharacterService;
    private final ArtisticFeatureService artisticFeatureService;

    // ── PUBLIC APIs (Dành cho độc giả) ───────────────────────────────────────
    @GetMapping
    @Operation(summary = "Lấy danh sách Tác phẩm")
    public ResponseEntity<ApiResponse<Page<WorkSummaryResponse>>> getWorks(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tag,
            @ParameterObject @PageableDefault(size = 24, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        ApiResponse<Page<WorkSummaryResponse>> response = ApiResponse.<Page<WorkSummaryResponse>>builder()
                .message("Lấy danh sách tác phẩm thành công")
                .result(workService.getWorksByFilter(genre, period, search, tag, pageable))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Lấy chi tiết Tác phẩm", description = "Lấy toàn bộ thông tin chi tiết của một tác phẩm...")
    public ResponseEntity<ApiResponse<WorkDetailResponse>> getWorkDetail(@PathVariable String slug) {

        ApiResponse<WorkDetailResponse> response = ApiResponse.<WorkDetailResponse>builder()
                .message("Lấy chi tiết tác phẩm thành công")
                .result(workService.getWorkDetail(slug))
                .build();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{workId}/sections")
    @Operation(summary = "Lấy danh sách phần văn bản của tác phẩm")
    public ResponseEntity<ApiResponse<List<WorkSectionSummaryResponse>>> getSections(
            @PathVariable UUID workId
    ) {
        List<WorkSectionSummaryResponse> result = workSectionService.getSections(workId);

        ApiResponse<List<WorkSectionSummaryResponse>> response =
                ApiResponse.<List<WorkSectionSummaryResponse>>builder()
                        .message("Lấy danh sách phần văn bản thành công")
                        .result(result)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{workId}/sections/{sectionId}")
    @Operation(summary = "Lấy nội dung một phần văn bản")
    public ResponseEntity<ApiResponse<WorkSectionDetailResponse>> getSection(
            @PathVariable UUID workId,
            @PathVariable UUID sectionId
    ) {
        WorkSectionDetailResponse result = workSectionService.getSection(workId, sectionId);

        ApiResponse<WorkSectionDetailResponse> response =
                ApiResponse.<WorkSectionDetailResponse>builder()
                        .message("Lấy nội dung phần văn bản thành công")
                        .result(result)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{workId}/characters")
    @Operation(summary = "Lấy danh sách nhân vật của tác phẩm")
    public ResponseEntity<ApiResponse<List<WorkCharacterResponse>>> getCharacters(
            @PathVariable UUID workId
    ) {
        List<WorkCharacterResponse> result = workCharacterService.getCharacters(workId);

        ApiResponse<List<WorkCharacterResponse>> response =
                ApiResponse.<List<WorkCharacterResponse>>builder()
                        .message("Lấy danh sách nhân vật thành công")
                        .result(result)
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{workId}/artistic-features")
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

    // ── ADMIN APIs (Dành cho Quản trị viên) ───────────────────────────────────
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo mới tác phẩm", description = "Yêu cầu quyền ADMIN")
    public ResponseEntity<ApiResponse<WorkDetailResponse>> createWork(@Valid @RequestBody WorkRequest request) {

        ApiResponse<WorkDetailResponse> response = ApiResponse.<WorkDetailResponse>builder()
                .message("Tạo mới tác phẩm thành công")
                .result(workService.createWork(request))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật tác phẩm", description = "Yêu cầu quyền ADMIN")
    public ResponseEntity<ApiResponse<WorkDetailResponse>> updateWork(
            @PathVariable UUID id,
            @Valid @RequestBody WorkRequest request
    ) {
        ApiResponse<WorkDetailResponse> response = ApiResponse.<WorkDetailResponse>builder()
                .message("Cập nhật tác phẩm thành công")
                .result(workService.updateWork(id, request))
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa tác phẩm", description = "Yêu cầu quyền ADMIN")
    public ResponseEntity<ApiResponse<Void>> deleteWork(@PathVariable UUID id) {

        workService.deleteWork(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/{workId}/sections")
    @Operation(summary = "Tạo phần văn bản mới")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WorkSectionDetailResponse>> createSection(
            @PathVariable UUID workId,
            @Valid @RequestBody CreateWorkSectionRequest request
    ) {
        WorkSectionDetailResponse result = workSectionService.createSection(workId, request);

        ApiResponse<WorkSectionDetailResponse> response =
                ApiResponse.<WorkSectionDetailResponse>builder()
                        .message("Tạo phần văn bản thành công")
                        .result(result)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/admin/{workId}/sections/{sectionId}")
    @Operation(summary = "Cập nhật phần văn bản")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WorkSectionDetailResponse>> updateSection(
            @PathVariable UUID workId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody UpdateWorkSectionRequest request
    ) {
        WorkSectionDetailResponse result = workSectionService.updateSection(workId, sectionId, request);

        ApiResponse<WorkSectionDetailResponse> response =
                ApiResponse.<WorkSectionDetailResponse>builder()
                        .message("Cập nhật phần văn bản thành công")
                        .result(result)
                        .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/{workId}/sections/{sectionId}")
    @Operation(summary = "Xóa phần văn bản")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSection(
            @PathVariable UUID workId,
            @PathVariable UUID sectionId
    ) {
        workSectionService.deleteSection(workId, sectionId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/{workId}/characters")
    @Operation(summary = "Tạo nhân vật")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WorkCharacterResponse>> createCharacter(
            @PathVariable UUID workId,
            @Valid @RequestBody CreateWorkCharacterRequest request
    ) {
        WorkCharacterResponse result = workCharacterService.createCharacter(workId, request);

        ApiResponse<WorkCharacterResponse> response =
                ApiResponse.<WorkCharacterResponse>builder()
                        .message("Tạo nhân vật thành công")
                        .result(result)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/admin/{workId}/characters/{characterId}")
    @Operation(summary = "Cập nhật nhân vật")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WorkCharacterResponse>> updateCharacter(
            @PathVariable UUID workId,
            @PathVariable UUID characterId,
            @Valid @RequestBody UpdateWorkCharacterRequest request
    ) {
        WorkCharacterResponse result = workCharacterService.updateCharacter(workId, characterId, request);

        ApiResponse<WorkCharacterResponse> response =
                ApiResponse.<WorkCharacterResponse>builder()
                        .message("Cập nhật nhân vật thành công")
                        .result(result)
                        .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/{workId}/characters/{characterId}")
    @Operation(summary = "Xóa nhân vật")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCharacter(
            @PathVariable UUID workId,
            @PathVariable UUID characterId
    ) {
        workCharacterService.deleteCharacter(workId, characterId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/{workId}/artistic-features")
    @Operation(summary = "Tạo đặc điểm nghệ thuật")
    @PreAuthorize("hasRole('ADMIN')")
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

    @PatchMapping("/admin/{workId}/artistic-features/{featureId}")
    @Operation(summary = "Cập nhật đặc điểm nghệ thuật")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ArtisticFeatureResponse>> updateArtisticFeature(
            @PathVariable UUID workId,
            @PathVariable UUID featureId,
            @Valid @RequestBody UpdateArtisticFeatureRequest request
    ) {
        ArtisticFeatureResponse result = artisticFeatureService.updateArtisticFeature(workId, featureId, request);

        ApiResponse<ArtisticFeatureResponse> response =
                ApiResponse.<ArtisticFeatureResponse>builder()
                        .message("Cập nhật đặc điểm nghệ thuật thành công")
                        .result(result)
                        .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/{workId}/artistic-features/{featureId}")
    @Operation(summary = "Xóa đặc điểm nghệ thuật")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteArtisticFeature(
            @PathVariable UUID workId,
            @PathVariable UUID featureId
    ) {
        artisticFeatureService.deleteArtisticFeature(workId, featureId);

        return ResponseEntity.noContent().build();
    }
}
