package com.sba.lexilearnbe.modules.work.controller;

import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkCommentaryResponse;
import com.sba.lexilearnbe.modules.work.services.WorkCommentaryService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/works")
@RequiredArgsConstructor
@Tag(name = "Work Commentary", description = "Các API dành cho bình phẩm tác phẩm")
public class WorkCommentaryController {

    private final WorkCommentaryService commentaryService;

    @GetMapping("/{workId}/commentaries")
    @Operation(summary = "Lấy danh sách bình phẩm đã xuất bản")
    public ResponseEntity<ApiResponse<Page<WorkCommentaryResponse>>> getPublishedCommentaries(
            @PathVariable UUID workId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "displayOrder") Pageable pageable,
            HttpServletRequest servletRequest) {
        ApiResponse<Page<WorkCommentaryResponse>> response =
                ApiResponse.<Page<WorkCommentaryResponse>>builder()
                .code("success")
                .message("Lấy danh sách bình phẩm thành công")
                .result(commentaryService.getPublishedCommentaries(workId, pageable))
                .timestamp(LocalDateTime.now())
                .path(servletRequest.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/{workId}/commentaries")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy toàn bộ bình phẩm", description = "Bao gồm cả bản chưa xuất bản")
    public ResponseEntity<ApiResponse<Page<WorkCommentaryResponse>>> getAllCommentaries(
            @PathVariable UUID workId,
            @ParameterObject
            @PageableDefault(size = 10, sort = "displayOrder") Pageable pageable,
            HttpServletRequest servletRequest) {
        ApiResponse<Page<WorkCommentaryResponse>> response =
                ApiResponse.<Page<WorkCommentaryResponse>>builder()
                .code("success")
                .message("Lấy toàn bộ bình phẩm thành công")
                .result(commentaryService.getAllCommentaries(workId, pageable))
                .timestamp(LocalDateTime.now())
                .path(servletRequest.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/{workId}/commentaries")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo bình phẩm")
    public ResponseEntity<ApiResponse<WorkCommentaryResponse>> createCommentary(
            @PathVariable UUID workId,
            @Valid @RequestBody CreateWorkCommentaryRequest request,
            HttpServletRequest servletRequest) {
        ApiResponse<WorkCommentaryResponse> response =
                ApiResponse.<WorkCommentaryResponse>builder()
                .code("success")
                .message("Tạo bình phẩm thành công")
                .result(commentaryService.createCommentary(workId, request))
                .timestamp(LocalDateTime.now())
                .path(servletRequest.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/admin/{workId}/commentaries/{commentaryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật bình phẩm")
    public ResponseEntity<ApiResponse<WorkCommentaryResponse>> updateCommentary(
            @PathVariable UUID workId,
            @PathVariable UUID commentaryId,
            @Valid @RequestBody UpdateWorkCommentaryRequest request,
            HttpServletRequest servletRequest) {
        ApiResponse<WorkCommentaryResponse> response =
                ApiResponse.<WorkCommentaryResponse>builder()
                .code("success")
                .message("Cập nhật bình phẩm thành công")
                .result(commentaryService.updateCommentary(workId, commentaryId, request))
                .timestamp(LocalDateTime.now())
                .path(servletRequest.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/admin/{workId}/commentaries/{commentaryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa bình phẩm")
    public ResponseEntity<ApiResponse<Void>> deleteCommentary(
            @PathVariable UUID workId,
            @PathVariable UUID commentaryId,
            HttpServletRequest servletRequest) {
        commentaryService.deleteCommentary(workId, commentaryId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("success")
                .message("Xóa bình phẩm thành công")
                .timestamp(LocalDateTime.now())
                .path(servletRequest.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }
}
