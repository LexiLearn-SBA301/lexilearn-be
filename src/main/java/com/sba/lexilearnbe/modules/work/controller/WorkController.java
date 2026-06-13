package com.sba.lexilearnbe.modules.work.controller;

import com.sba.lexilearnbe.modules.work.dto.response.WorkDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSummaryResponse;
import com.sba.lexilearnbe.modules.work.services.WorkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/works")
@RequiredArgsConstructor
@Tag(name = "Work", description = "Các API dành cho Thư viện tác phẩm")
public class WorkController {

    private final WorkService workService;

    // API Lấy danh sách + Lọc (Filter) + Tìm kiếm (Search) + Phân trang
    @GetMapping
    @Operation(
            summary = "Lấy danh sách Tác phẩm",
            description = "Lấy danh sách tất cả tác phẩm trong thư viện. Hỗ trợ lọc theo thể loại (genre), thời kỳ văn học (period), tìm kiếm theo từ khóa (search) và phân trang."
    )
    public ResponseEntity<Page<WorkSummaryResponse>> getWorks(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "24") int size,
            @RequestParam(defaultValue = "viewCount,desc") String sort // Mặc định hiển thị sách nổi bật nhất
    ) {
        // Truyền thêm biến search xuống Service
        Page<WorkSummaryResponse> response = workService.getWorksByFilter(genre, period, search, page, size, sort);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{slug}")
    @Operation(
            summary = "Lấy chi tiết Tác phẩm",
            description = "Lấy toàn bộ thông tin chi tiết của một tác phẩm (bao gồm bối cảnh lịch sử, giá trị hiện thực, trích dẫn hay...) dựa vào slug."
    )
    public ResponseEntity<WorkDetailResponse> getWorkDetail(@PathVariable String slug) {
        WorkDetailResponse response = workService.getWorkDetail(slug);
        return ResponseEntity.ok(response);
    }
}