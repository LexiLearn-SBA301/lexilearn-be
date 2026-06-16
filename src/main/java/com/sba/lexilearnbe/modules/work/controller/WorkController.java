package com.sba.lexilearnbe.modules.work.controller;

import com.sba.lexilearnbe.modules.work.dto.request.WorkRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSummaryResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Work", description = "Các API dành cho Thư viện tác phẩm")
public class WorkController {

    private final WorkService workService;

    // ── PUBLIC APIs (Dành cho độc giả) ───────────────────────────────────────
    @GetMapping("/works")
    @Operation(summary = "Lấy danh sách Tác phẩm")
    public ResponseEntity<ApiResponse<Page<WorkSummaryResponse>>> getWorks(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String search,
            @ParameterObject @PageableDefault(size = 24, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        ApiResponse<Page<WorkSummaryResponse>> response = ApiResponse.<Page<WorkSummaryResponse>>builder()
                .message("Lấy danh sách tác phẩm thành công")
                .result(workService.getWorksByFilter(genre, period, search, pageable))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/works/{slug}")
    @Operation(summary = "Lấy chi tiết Tác phẩm", description = "Lấy toàn bộ thông tin chi tiết của một tác phẩm...")
    public ResponseEntity<ApiResponse<WorkDetailResponse>> getWorkDetail(@PathVariable String slug) {

        ApiResponse<WorkDetailResponse> response = ApiResponse.<WorkDetailResponse>builder()
                .message("Lấy chi tiết tác phẩm thành công")
                .result(workService.getWorkDetail(slug))
                .build();

        return ResponseEntity.ok(response);
    }
    @PostMapping("/admin/works")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo mới tác phẩm", description = "Yêu cầu quyền ADMIN")
    public ResponseEntity<ApiResponse<WorkDetailResponse>> createWork(@Valid @RequestBody WorkRequest request) {

        ApiResponse<WorkDetailResponse> response = ApiResponse.<WorkDetailResponse>builder()
                .message("Tạo mới tác phẩm thành công")
                .result(workService.createWork(request))
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/admin/works/{id}")
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

    @DeleteMapping("/admin/works/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa tác phẩm", description = "Yêu cầu quyền ADMIN")
    public ResponseEntity<ApiResponse<Void>> deleteWork(@PathVariable UUID id) {

        workService.deleteWork(id);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .message("Xóa tác phẩm thành công")
                .build();

        return ResponseEntity.ok(response);
    }
}