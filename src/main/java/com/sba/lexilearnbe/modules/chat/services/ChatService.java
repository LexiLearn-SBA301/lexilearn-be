package com.sba.lexilearnbe.modules.chat.services;

import com.sba.lexilearnbe.modules.chat.dto.request.SendMessageRequest;
import com.sba.lexilearnbe.modules.chat.dto.response.ConversationDetailResponse;
import com.sba.lexilearnbe.modules.chat.dto.response.ConversationSummaryResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    /** Danh sách hội thoại của user (sidebar), mới nhất trước. */
    List<ConversationSummaryResponse> listConversations(UUID accountId);

    /** Mở lại 1 đoạn cũ: trả transcript + seed lại lịch sử cho AI để sẵn sàng chat tiếp. */
    ConversationDetailResponse openConversation(UUID accountId, UUID conversationId);

    /** Xóa 1 đoạn của user. */
    void deleteConversation(UUID accountId, UUID conversationId);

    /** Gửi tin nhắn -> relay stream từ AI về FE qua SSE (chạy nền). */
    SseEmitter streamMessage(UUID accountId, SendMessageRequest request);
}
