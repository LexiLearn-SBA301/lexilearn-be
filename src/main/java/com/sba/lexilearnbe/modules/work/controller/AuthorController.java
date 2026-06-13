package com.sba.lexilearnbe.modules.work.controller;

import com.sba.lexilearnbe.modules.work.dto.response.AuthorDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.AuthorSummaryResponse;
import com.sba.lexilearnbe.modules.work.services.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<Page<AuthorSummaryResponse>> getAuthors(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {

        Page<AuthorSummaryResponse> response = authorService.getAuthors(search, page, size, sort);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Lấy chi tiết Tác giả", description = "Lấy thông tin chi tiết tiểu sử của tác giả dựa vào slug")
    public ResponseEntity<AuthorDetailResponse> getAuthorDetail(@PathVariable String slug) {
        AuthorDetailResponse response = authorService.getAuthorDetail(slug);
        return ResponseEntity.ok(response);
    }
}