package com.sba.lexilearnbe.modules.work.controller;

import com.sba.lexilearnbe.modules.work.dto.request.AuthorRequest;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorSummaryResponse;
import com.sba.lexilearnbe.modules.work.services.AuthorService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
@Tag(name = "Author", description = "Các API dành cho dữ liệu Tác giả")
public class AuthorController {

    private final AuthorService authorService;

    // ── PUBLIC APIs (Dành cho độc giả) ───────────────────────────────────────
    @GetMapping
    @Operation(summary = "Lấy danh sách Tác giả", description = "Lấy danh sách tất cả tác giả, có hỗ trợ tìm kiếm và phân trang")
    public ResponseEntity<ApiResponse<Page<AuthorSummaryResponse>>> getAuthors(
            @RequestParam(required = false) String search,
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable) {

        ApiResponse<Page<AuthorSummaryResponse>> response = ApiResponse.<Page<AuthorSummaryResponse>>builder()
                .message("Lấy danh sách tác giả thành công")
                .result(authorService.getAuthors(search, pageable))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Lấy chi tiết Tác giả", description = "Lấy thông tin chi tiết tiểu sử của tác giả dựa vào slug")
    public ResponseEntity<ApiResponse<AuthorDetailResponse>> getAuthorDetail(@PathVariable String slug) {

        ApiResponse<AuthorDetailResponse> response = ApiResponse.<AuthorDetailResponse>builder()
                .message("Lấy chi tiết tác giả thành công")
                .result(authorService.getAuthorDetail(slug))
                .build();

        return ResponseEntity.ok(response);
    }
    // ── ADMIN APIs (Dành cho Quản trị viên) ───────────────────────────────────
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo mới tác giả", description = "Yêu cầu quyền ADMIN")
    public ResponseEntity<ApiResponse<AuthorDetailResponse>> createAuthor(@Valid @RequestBody AuthorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<AuthorDetailResponse>builder()
                .message("Tạo mới tác giả thành công")
                .result(authorService.createAuthor(request))
                .build());
    }

    @PatchMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật thông tin tác giả", description = "Yêu cầu quyền ADMIN")
    public ResponseEntity<ApiResponse<AuthorDetailResponse>> updateAuthor(
            @PathVariable UUID id,
            @Valid @RequestBody AuthorRequest request) {

        return ResponseEntity.ok(ApiResponse.<AuthorDetailResponse>builder()
                .message("Cập nhật tác giả thành công")
                .result(authorService.updateAuthor(id, request))
                .build());
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa tác giả", description = "Yêu cầu quyền ADMIN")
    public ResponseEntity<ApiResponse<Void>> deleteAuthor(@PathVariable UUID id) {
        authorService.deleteAuthor(id);

        return ResponseEntity.noContent().build();
    }
}