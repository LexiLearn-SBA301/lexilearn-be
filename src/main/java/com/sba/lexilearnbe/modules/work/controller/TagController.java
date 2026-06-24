package com.sba.lexilearnbe.modules.work.controller;

import com.sba.lexilearnbe.modules.work.dto.request.TagRequest;
import com.sba.lexilearnbe.modules.work.dto.response.TagResponse;
import com.sba.lexilearnbe.modules.work.services.TagService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag", description = "Các API dành cho dữ liệu Thẻ phân loại")
public class TagController {

    private final TagService tagService;

    // ── PUBLIC APIs (Dành cho độc giả) ───────────────────────────────────────
    @GetMapping("/tags")
    @Operation(summary = "Lấy danh sách Thẻ", description = "Hỗ trợ phân trang và tìm kiếm theo tên")
    public ResponseEntity<ApiResponse<Page<TagResponse>>> getAllTags(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());

        ApiResponse<Page<TagResponse>> response = ApiResponse.<Page<TagResponse>>builder()
                .message("Lấy danh sách thẻ phân loại thành công")
                .result(tagService.getAllTags(search, pageable))
                .build();

        return ResponseEntity.ok(response);
    }
    // ── ADMIN APIs (Dành cho Quản trị viên) ───────────────────────────────────
    @PostMapping("/admin/tags")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo Thẻ mới", description = "Yêu cầu quyền ADMIN")
    public ResponseEntity<ApiResponse<TagResponse>> createTag(@Valid @RequestBody TagRequest request) {
        ApiResponse<TagResponse> response = ApiResponse.<TagResponse>builder()
                .message("Tạo thẻ thành công")
                .result(tagService.createTag(request))
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PatchMapping("/admin/tags/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật Thẻ", description = "Yêu cầu quyền ADMIN")
    public ResponseEntity<ApiResponse<TagResponse>> updateTag(@PathVariable UUID id, @Valid @RequestBody TagRequest request) {
        ApiResponse<TagResponse> response = ApiResponse.<TagResponse>builder()
                .message("Cập nhật thẻ thành công")
                .result(tagService.updateTag(id, request))
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/tags/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa Thẻ", description = "Yêu cầu quyền ADMIN")
    public ResponseEntity<ApiResponse<Void>> deleteTag(@PathVariable UUID id) {

        tagService.deleteTag(id);

        return ResponseEntity.noContent().build();
    }
}