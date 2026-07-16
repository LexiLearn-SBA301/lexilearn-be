package com.sba.lexilearnbe.modules.chat.controller;

import com.sba.lexilearnbe.modules.chat.dto.request.SendMessageRequest;
import com.sba.lexilearnbe.modules.chat.dto.response.ConversationDetailResponse;
import com.sba.lexilearnbe.modules.chat.dto.response.ConversationSummaryResponse;
import com.sba.lexilearnbe.modules.chat.services.ChatService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * API chat cho FE. FE CHỈ nói chuyện với BE (không gọi thẳng AI) -> auth tập trung, AI ẩn nội bộ.
 * Mọi endpoint đều yêu cầu đăng nhập (SecurityConfig: anyRequest().authenticated()); accountId
 * lấy từ JWT qua @AuthenticationPrincipal.
 */
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Hội thoại với trợ lý AI + lịch sử chat cá nhân")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/conversations")
    @Operation(summary = "Danh sách hội thoại của người dùng (sidebar)")
    public ResponseEntity<ApiResponse<List<ConversationSummaryResponse>>> listConversations(
            @AuthenticationPrincipal UUID accountId,
            HttpServletRequest servletRequest
    ) {
        ApiResponse<List<ConversationSummaryResponse>> response = ApiResponse.<List<ConversationSummaryResponse>>builder()
                .code("success")
                .message("Lấy danh sách hội thoại thành công")
                .result(chatService.listConversations(accountId))
                .timestamp(LocalDateTime.now())
                .path(servletRequest.getRequestURI())
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversations/{conversationId}")
    @Operation(summary = "Mở lại 1 đoạn chat cũ: lấy transcript + chuẩn bị AI để chat tiếp")
    public ResponseEntity<ApiResponse<ConversationDetailResponse>> openConversation(
            @AuthenticationPrincipal UUID accountId,
            @PathVariable UUID conversationId,
            HttpServletRequest servletRequest
    ) {
        ApiResponse<ConversationDetailResponse> response = ApiResponse.<ConversationDetailResponse>builder()
                .code("success")
                .message("Mở hội thoại thành công")
                .result(chatService.openConversation(accountId, conversationId))
                .timestamp(LocalDateTime.now())
                .path(servletRequest.getRequestURI())
                .build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/conversations/messages")
    @Operation(summary = "Gửi tin nhắn — trả về SSE stream (tiến trình + câu trả lời) relay từ AI")
    public SseEmitter sendMessage(
            @AuthenticationPrincipal UUID accountId,
            @Valid @RequestBody SendMessageRequest request
    ) {
        // conversationId=null trong body -> BE lazy-create đoạn mới, báo id thực ở event SSE đầu tiên.
        return chatService.streamMessage(accountId, request);
    }

    @DeleteMapping("/conversations/{conversationId}")
    @Operation(summary = "Xóa 1 đoạn hội thoại")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(
            @AuthenticationPrincipal UUID accountId,
            @PathVariable UUID conversationId,
            HttpServletRequest servletRequest
    ) {
        chatService.deleteConversation(accountId, conversationId);
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code("success")
                .message("Xóa hội thoại thành công")
                .timestamp(LocalDateTime.now())
                .path(servletRequest.getRequestURI())
                .build();
        return ResponseEntity.ok(response);
    }
}
