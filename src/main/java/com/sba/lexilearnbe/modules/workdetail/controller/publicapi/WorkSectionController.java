package com.sba.lexilearnbe.modules.workdetail.controller.publicapi;

import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkSectionDetailResponse;
import com.sba.lexilearnbe.modules.workdetail.dto.response.WorkSectionSummaryResponse;
import com.sba.lexilearnbe.modules.workdetail.services.WorkSectionService;
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
@Tag(name = "Work Section", description = "API đọc nội dung tác phẩm")
public class WorkSectionController {

    private final WorkSectionService workSectionService;

    @GetMapping("/works/{workId}/sections")
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

    @GetMapping("/sections/{sectionId}")
    @Operation(summary = "Lấy nội dung một phần văn bản")
    public ResponseEntity<ApiResponse<WorkSectionDetailResponse>> getSection(
            @PathVariable UUID sectionId
    ) {
        WorkSectionDetailResponse result = workSectionService.getSection(sectionId);

        ApiResponse<WorkSectionDetailResponse> response =
                ApiResponse.<WorkSectionDetailResponse>builder()
                        .message("Lấy nội dung phần văn bản thành công")
                        .result(result)
                        .build();

        return ResponseEntity.ok(response);
    }
}
