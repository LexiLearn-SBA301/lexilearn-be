package com.sba.lexilearnbe.modules.work.controller;

import com.sba.lexilearnbe.modules.work.dto.request.ModerateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.response.AdminWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.services.WorkReviewService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/review-revisions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Work Review", description = "Kiểm duyệt bình phẩm của độc giả")
public class AdminWorkReviewController {

    private final WorkReviewService reviewService;

    @GetMapping
    @Operation(summary = "Lấy danh sách phiên bản bình phẩm theo trạng thái")
    public ResponseEntity<ApiResponse<Page<AdminWorkReviewResponse>>> getReviews(
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            HttpServletRequest servletRequest) {
        ApiResponse<Page<AdminWorkReviewResponse>> response =
                ApiResponse.<Page<AdminWorkReviewResponse>>builder()
                        .code("success")
                        .message("Lấy danh sách kiểm duyệt bình phẩm thành công")
                        .result(reviewService.getReviewsForModeration(
                                status, page, size, sortDir, sortBy
                        ))
                        .timestamp(LocalDateTime.now())
                        .path(servletRequest.getRequestURI())
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{revisionId}")
    @Operation(summary = "Lấy chi tiết phiên bản cần kiểm duyệt")
    public ResponseEntity<ApiResponse<AdminWorkReviewResponse>> getReviewDetail(
            @PathVariable UUID revisionId,
            HttpServletRequest servletRequest) {
        ApiResponse<AdminWorkReviewResponse> response =
                ApiResponse.<AdminWorkReviewResponse>builder()
                        .code("success")
                        .message("Lấy chi tiết kiểm duyệt bình phẩm thành công")
                        .result(reviewService.getModerationDetail(revisionId))
                        .timestamp(LocalDateTime.now())
                        .path(servletRequest.getRequestURI())
                        .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{revisionId}/moderation")
    @Operation(summary = "Duyệt hoặc từ chối phiên bản bình phẩm")
    public ResponseEntity<ApiResponse<AdminWorkReviewResponse>> moderateReview(
            @AuthenticationPrincipal UUID adminId,
            @PathVariable UUID revisionId,
            @Valid @RequestBody ModerateWorkReviewRequest request,
            HttpServletRequest servletRequest) {
        ApiResponse<AdminWorkReviewResponse> response =
                ApiResponse.<AdminWorkReviewResponse>builder()
                        .code("success")
                        .message("Kiểm duyệt bình phẩm thành công")
                        .result(reviewService.moderateReview(
                                adminId, revisionId, request
                        ))
                        .timestamp(LocalDateTime.now())
                        .path(servletRequest.getRequestURI())
                        .build();

        return ResponseEntity.ok(response);
    }
}
