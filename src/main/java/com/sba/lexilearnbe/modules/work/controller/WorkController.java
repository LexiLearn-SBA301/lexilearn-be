package com.sba.lexilearnbe.modules.work.controller;

import com.sba.lexilearnbe.modules.work.dto.response.WorkDetailResponse;
import com.sba.lexilearnbe.modules.work.dto.response.WorkSummaryResponse;
import com.sba.lexilearnbe.modules.work.services.WorkService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/works")
@RequiredArgsConstructor
@Tag(name = "Work", description = "Các API dành cho Thư viện tác phẩm")
public class WorkController {

    private final WorkService workService;

    @GetMapping
    @Operation(summary = "Lấy danh sách Tác phẩm")
    public ApiResponse<Page<WorkSummaryResponse>> getWorks(
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String search,
            @ParameterObject @PageableDefault(size = 24, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ApiResponse.<Page<WorkSummaryResponse>>builder()
                .message("Lấy danh sách tác phẩm thành công")
                .result(workService.getWorksByFilter(genre, period, search, pageable))
                .build();
    }


    @GetMapping("/{slug}")
    @Operation(
            summary = "Lấy chi tiết Tác phẩm",
            description = "Lấy toàn bộ thông tin chi tiết của một tác phẩm (bao gồm bối cảnh lịch sử, giá trị hiện thực, trích dẫn hay...) dựa vào slug."
    )
    public ApiResponse<WorkDetailResponse> getWorkDetail(@PathVariable String slug) {
        return ApiResponse.<WorkDetailResponse>builder()
                .message("Lấy chi tiết tác phẩm thành công")
                .result(workService.getWorkDetail(slug))
                .build();
    }
}