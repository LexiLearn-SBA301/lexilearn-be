package com.sba.lexilearnbe.modules.reading.controller;

import com.sba.lexilearnbe.modules.reading.dto.request.CreateNoteRequest;
import com.sba.lexilearnbe.modules.reading.dto.response.NoteResponse;
import com.sba.lexilearnbe.modules.reading.services.NoteService;
import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Note", description = "API highlight và ghi chú cá nhân")
public class NoteController {

    private final NoteService noteService;

    @GetMapping("/sections/{sectionId}/notes")
    @Operation(summary = "Lấy ghi chú của người dùng trong một phần văn bản")
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getNotes(
            @AuthenticationPrincipal UUID accountId,
            @PathVariable UUID sectionId,
            HttpServletRequest servletRequest
    ) {
        ApiResponse<List<NoteResponse>> response = ApiResponse.<List<NoteResponse>>builder()
                .code("success")
                .message("Lấy danh sách ghi chú thành công")
                .result(noteService.getNotes(accountId, sectionId))
                .timestamp(LocalDateTime.now())
                .path(servletRequest.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/sections/{sectionId}/notes")
    @Operation(summary = "Tạo highlight hoặc ghi chú")
    public ResponseEntity<ApiResponse<NoteResponse>> createNote(
            @AuthenticationPrincipal UUID accountId,
            @PathVariable UUID sectionId,
            @Valid @RequestBody CreateNoteRequest request,
            HttpServletRequest servletRequest
    ) {
        ApiResponse<NoteResponse> response = ApiResponse.<NoteResponse>builder()
                .code("success")
                .message("Tạo ghi chú thành công")
                .result(noteService.createNote(accountId, sectionId, request))
                .timestamp(LocalDateTime.now())
                .path(servletRequest.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/notes/{noteId}")
    @Operation(summary = "Xóa ghi chú")
    public ResponseEntity<ApiResponse<Void>> deleteNote(
            @AuthenticationPrincipal UUID accountId,
            @PathVariable UUID noteId,
            HttpServletRequest servletRequest
    ) {
        noteService.deleteNote(accountId, noteId);

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("success")
                .message("Xóa ghi chú thành công")
                .timestamp(LocalDateTime.now())
                .path(servletRequest.getRequestURI())
                .build();

        return ResponseEntity.ok(response);
    }
}
