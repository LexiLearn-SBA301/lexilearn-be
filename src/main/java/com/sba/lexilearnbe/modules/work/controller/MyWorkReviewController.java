package com.sba.lexilearnbe.modules.work.controller;

import com.sba.lexilearnbe.modules.work.dto.request.UpdateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.response.MyWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.services.WorkReviewService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/v1/me/reviews")
@RequiredArgsConstructor
@Tag(name = "My Work Review", description = "Quản lý bình phẩm của người dùng hiện tại")
public class MyWorkReviewController {

    private final WorkReviewService reviewService;

    @GetMapping
    @Operation(summary = "Lấy danh sách bình phẩm của tôi")
    public ResponseEntity<ApiResponse<Page<MyWorkReviewResponse>>> getMyReviews(
            @AuthenticationPrincipal UUID accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            HttpServletRequest servletRequest) {
        ApiResponse<Page<MyWorkReviewResponse>> response =
                ApiResponse.<Page<MyWorkReviewResponse>>builder()
                        .code("success")
                        .message("Lấy danh sách bình phẩm của tôi thành công")
                        .result(reviewService.getMyReviews(
                                accountId, page, size, sortDir, sortBy
                        ))
                        .timestamp(LocalDateTime.now())
                        .path(servletRequest.getRequestURI())
                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "Lấy chi tiết bình phẩm của tôi")
    public ResponseEntity<ApiResponse<MyWorkReviewResponse>> getMyReview(
            @AuthenticationPrincipal UUID accountId,
            @PathVariable UUID reviewId,
            HttpServletRequest servletRequest) {
        ApiResponse<MyWorkReviewResponse> response =
                ApiResponse.<MyWorkReviewResponse>builder()
                        .code("success")
                        .message("Lấy chi tiết bình phẩm thành công")
                        .result(reviewService.getMyReview(accountId, reviewId))
                        .timestamp(LocalDateTime.now())
                        .path(servletRequest.getRequestURI())
                        .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reviewId}")
    @Operation(summary = "Cập nhật bình phẩm của tôi")
    public ResponseEntity<ApiResponse<MyWorkReviewResponse>> updateMyReview(
            @AuthenticationPrincipal UUID accountId,
            @PathVariable UUID reviewId,
            @Valid @RequestBody UpdateWorkReviewRequest request,
            HttpServletRequest servletRequest) {
        ApiResponse<MyWorkReviewResponse> response =
                ApiResponse.<MyWorkReviewResponse>builder()
                        .code("success")
                        .message("Cập nhật bình phẩm thành công")
                        .result(reviewService.updateMyReview(
                                accountId, reviewId, request
                        ))
                        .timestamp(LocalDateTime.now())
                        .path(servletRequest.getRequestURI())
                        .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "Xóa bình phẩm của tôi")
    public ResponseEntity<ApiResponse<Void>> deleteMyReview(
            @AuthenticationPrincipal UUID accountId,
            @PathVariable UUID reviewId,
            HttpServletRequest servletRequest) {
        reviewService.deleteMyReview(accountId, reviewId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("success")
                .message("Xóa bình phẩm thành công")
                .timestamp(LocalDateTime.now())
                .path(servletRequest.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }
}
