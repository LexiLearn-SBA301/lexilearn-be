package com.sba.lexilearnbe.modules.reading.controller;

import com.sba.lexilearnbe.modules.reading.dto.request.UpsertBookmarkRequest;
import com.sba.lexilearnbe.modules.reading.dto.response.BookmarkResponse;
import com.sba.lexilearnbe.modules.reading.services.BookmarkService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/me/bookmarks")
@RequiredArgsConstructor
@Tag(name = "Bookmark", description = "API lưu tiến độ đọc của người dùng")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tác phẩm đang đọc")
    public ResponseEntity<ApiResponse<List<BookmarkResponse>>> getBookmarks(
            @AuthenticationPrincipal UUID accountId,
            HttpServletRequest servletRequest
    ) {
        ApiResponse<List<BookmarkResponse>> response = ApiResponse.<List<BookmarkResponse>>builder()
                .code("success")
                .message("Lấy danh sách bookmark thành công")
                .result(bookmarkService.getBookmarks(accountId))
                .timestamp(LocalDateTime.now())
                .path(servletRequest.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{workId}")
    @Operation(summary = "Tạo hoặc cập nhật tiến độ đọc")
    public ResponseEntity<ApiResponse<BookmarkResponse>> upsertBookmark(
            @AuthenticationPrincipal UUID accountId,
            @PathVariable UUID workId,
            @Valid @RequestBody UpsertBookmarkRequest request,
            HttpServletRequest servletRequest
    ) {
        ApiResponse<BookmarkResponse> response = ApiResponse.<BookmarkResponse>builder()
                .code("success")
                .message("Cập nhật bookmark thành công")
                .result(bookmarkService.upsertBookmark(accountId, workId, request))
                .timestamp(LocalDateTime.now())
                .path(servletRequest.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{workId}")
    @Operation(summary = "Xóa bookmark của tác phẩm")
    public ResponseEntity<ApiResponse<Void>> deleteBookmark(
            @AuthenticationPrincipal UUID accountId,
            @PathVariable UUID workId,
            HttpServletRequest servletRequest
    ) {
        bookmarkService.deleteBookmark(accountId, workId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("success")
                .message("Xóa bookmark thành công")
                .timestamp(LocalDateTime.now())
                .path(servletRequest.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }
}
