package com.sba.lexilearnbe.modules.work.controller;

import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkCommentaryRequest;
import com.sba.lexilearnbe.modules.work.dto.response.WorkCommentaryResponse;
import com.sba.lexilearnbe.modules.work.services.WorkCommentaryService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/works")
@RequiredArgsConstructor
@Tag(name = "Work Commentary", description = "Các API dành cho bình phẩm tác phẩm")
public class WorkCommentaryController {

    private final WorkCommentaryService commentaryService;

    @GetMapping("/{workId}/commentaries")
    @Operation(summary = "Lấy danh sách bình phẩm đã xuất bản")
    public ResponseEntity<ApiResponse<List<WorkCommentaryResponse>>> getPublishedCommentaries(
            @PathVariable UUID workId) {
        return ResponseEntity.ok(ApiResponse.<List<WorkCommentaryResponse>>builder()
                .message("Lấy danh sách bình phẩm thành công")
                .result(commentaryService.getPublishedCommentaries(workId))
                .build());
    }

    @GetMapping("/admin/{workId}/commentaries")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lấy toàn bộ bình phẩm", description = "Bao gồm cả bản chưa xuất bản")
    public ResponseEntity<ApiResponse<List<WorkCommentaryResponse>>> getAllCommentaries(
            @PathVariable UUID workId) {
        return ResponseEntity.ok(ApiResponse.<List<WorkCommentaryResponse>>builder()
                .message("Lấy toàn bộ bình phẩm thành công")
                .result(commentaryService.getAllCommentaries(workId))
                .build());
    }

    @PostMapping("/admin/{workId}/commentaries")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo bình phẩm")
    public ResponseEntity<ApiResponse<WorkCommentaryResponse>> createCommentary(
            @PathVariable UUID workId,
            @Valid @RequestBody CreateWorkCommentaryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<WorkCommentaryResponse>builder()
                        .message("Tạo bình phẩm thành công")
                        .result(commentaryService.createCommentary(workId, request))
                        .build());
    }

    @PatchMapping("/admin/{workId}/commentaries/{commentaryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Cập nhật bình phẩm")
    public ResponseEntity<ApiResponse<WorkCommentaryResponse>> updateCommentary(
            @PathVariable UUID workId,
            @PathVariable UUID commentaryId,
            @Valid @RequestBody UpdateWorkCommentaryRequest request) {
        return ResponseEntity.ok(ApiResponse.<WorkCommentaryResponse>builder()
                .message("Cập nhật bình phẩm thành công")
                .result(commentaryService.updateCommentary(workId, commentaryId, request))
                .build());
    }

    @DeleteMapping("/admin/{workId}/commentaries/{commentaryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Xóa bình phẩm")
    public ResponseEntity<ApiResponse<Void>> deleteCommentary(
            @PathVariable UUID workId,
            @PathVariable UUID commentaryId) {
        commentaryService.deleteCommentary(workId, commentaryId);
        return ResponseEntity.noContent().build();
    }
}
