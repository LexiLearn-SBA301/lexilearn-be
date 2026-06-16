package com.sba.lexilearnbe.modules.workdetail.controller.admin;

import com.sba.lexilearnbe.modules.workdetail.dto.request.CreateWorkSectionRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.request.UpdateWorkSectionRequest;
import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkSectionDetailResponse;
import com.sba.lexilearnbe.modules.workdetail.services.WorkSectionService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Work Section", description = "API quản trị nội dung tác phẩm")
public class AdminWorkSectionController {

    private final WorkSectionService workSectionService;

    @PostMapping("/works/{workId}/sections")
    @Operation(summary = "Tạo phần văn bản mới")
    public ResponseEntity<ApiResponse<WorkSectionDetailResponse>> createSection(
            @PathVariable UUID workId,
            @Valid @RequestBody CreateWorkSectionRequest request
    ) {
        WorkSectionDetailResponse result =
                workSectionService.createSection(workId, request);

        ApiResponse<WorkSectionDetailResponse> response =
                ApiResponse.<WorkSectionDetailResponse>builder()
                        .message("Tạo phần văn bản thành công")
                        .result(result)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/sections/{sectionId}")
    @Operation(summary = "Cập nhật phần văn bản")
    public ResponseEntity<ApiResponse<WorkSectionDetailResponse>> updateSection(
            @PathVariable UUID sectionId,
            @Valid @RequestBody UpdateWorkSectionRequest request
    ) {
        WorkSectionDetailResponse result =
                workSectionService.updateSection(sectionId, request);

        ApiResponse<WorkSectionDetailResponse> response =
                ApiResponse.<WorkSectionDetailResponse>builder()
                        .message("Cập nhật phần văn bản thành công")
                        .result(result)
                        .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/sections/{sectionId}")
    @Operation(summary = "Xóa phần văn bản")
    public ResponseEntity<ApiResponse<Void>> deleteSection(
            @PathVariable UUID sectionId
    ) {
        workSectionService.deleteSection(sectionId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .message("Xóa phần văn bản thành công")
                .build();

        return ResponseEntity.ok(response);
    }
}