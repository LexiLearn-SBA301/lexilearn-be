package com.sba.lexilearnbe.modules.work.controller;

import com.sba.lexilearnbe.modules.work.dto.response.TagResponse;
import com.sba.lexilearnbe.modules.work.services.TagService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "Tag", description = "Các API dành cho dữ liệu Thẻ phân loại")
public class TagController {

    private final TagService tagService;

    @GetMapping
    @Operation(summary = "Lấy danh sách Thẻ", description = "Lấy toàn bộ danh sách các thẻ phân loại, sắp xếp theo thứ tự A-Z")
    public ResponseEntity<ApiResponse<List<TagResponse>>> getAllTags() {

        ApiResponse<List<TagResponse>> response = ApiResponse.<List<TagResponse>>builder()
                .message("Lấy danh sách thẻ phân loại thành công")
                .result(tagService.getAllTags())
                .build();

        return ResponseEntity.ok(response);
    }
}