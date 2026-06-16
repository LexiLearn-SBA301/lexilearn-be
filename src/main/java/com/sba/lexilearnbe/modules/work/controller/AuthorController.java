package com.sba.lexilearnbe.modules.work.controller;

import com.sba.lexilearnbe.modules.work.dto.response.AuthorDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorSummaryResponse;
import com.sba.lexilearnbe.modules.work.services.AuthorService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/authors")
@RequiredArgsConstructor
@Tag(name = "Author", description = "Các API dành cho dữ liệu Tác giả")
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    @Operation(summary = "Lấy danh sách Tác giả", description = "Lấy danh sách tất cả tác giả, có hỗ trợ tìm kiếm và phân trang")
    public ApiResponse<Page<AuthorSummaryResponse>> getAuthors(
            @RequestParam(required = false) String search,
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable) {

        return ApiResponse.<Page<AuthorSummaryResponse>>builder()
                .message("Lấy danh sách tác giả thành công")
                .result(authorService.getAuthors(search, pageable))
                .build();
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Lấy chi tiết Tác giả", description = "Lấy thông tin chi tiết tiểu sử của tác giả dựa vào slug")
    public ApiResponse<AuthorDetailResponse> getAuthorDetail(@PathVariable String slug) {
        return ApiResponse.<AuthorDetailResponse>builder()
                .message("Lấy chi tiết tác giả thành công")
                .result(authorService.getAuthorDetail(slug))
                .build();
    }
}