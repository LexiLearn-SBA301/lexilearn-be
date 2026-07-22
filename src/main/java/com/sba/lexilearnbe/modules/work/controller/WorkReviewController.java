package com.sba.lexilearnbe.modules.work.controller;

import com.sba.lexilearnbe.modules.work.dto.request.CreateWorkReviewRequest;
import com.sba.lexilearnbe.modules.work.dto.response.MyWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.dto.response.PublicWorkReviewResponse;
import com.sba.lexilearnbe.modules.work.services.WorkReviewService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/works")
@RequiredArgsConstructor
@Tag(name = "Work Review", description = "Bình phẩm do độc giả gửi")
public class WorkReviewController {

    private final WorkReviewService reviewService;

    @GetMapping("/{workId}/reviews")
    @Operation(summary = "Lấy bình phẩm công khai của tác phẩm")
    public ResponseEntity<ApiResponse<Page<PublicWorkReviewResponse>>> getPublishedReviews(
            @PathVariable UUID workId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "reviewedAt") String sortBy,
            HttpServletRequest servletRequest) {
        ApiResponse<Page<PublicWorkReviewResponse>> response =
                ApiResponse.<Page<PublicWorkReviewResponse>>builder()
                        .code("success")
                        .message("Lấy danh sách bình phẩm của độc giả thành công")
                        .result(reviewService.getPublishedReviews(workId, page, size, sortDir, sortBy))
                        .timestamp(LocalDateTime.now())
                        .path(servletRequest.getRequestURI())
                        .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{workId}/reviews")
    @Operation(summary = "Gửi bình phẩm mới cho tác phẩm", description = "Bình phẩm được hiển thị công khai ngay, không cần admin duyệt")
    public ResponseEntity<ApiResponse<MyWorkReviewResponse>> createReview(
            @AuthenticationPrincipal UUID accountId,
            @PathVariable UUID workId,
            @Valid @RequestBody CreateWorkReviewRequest request,
            HttpServletRequest servletRequest) {
        ApiResponse<MyWorkReviewResponse> response =
                ApiResponse.<MyWorkReviewResponse>builder()
                        .code("success")
                        .message("Gửi bình phẩm thành công")
                        .result(reviewService.createReview(accountId, workId, request))
                        .timestamp(LocalDateTime.now())
                        .path(servletRequest.getRequestURI())
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
