package com.sba.lexilearnbe.modules.chat.services;

import com.sba.lexilearnbe.modules.chat.dto.request.DebateReplyRequest;
import com.sba.lexilearnbe.modules.chat.dto.request.SendMessageRequest;
import com.sba.lexilearnbe.modules.chat.dto.request.SendSyncMessageRequest;
import com.sba.lexilearnbe.modules.chat.dto.response.ConversationDetailResponse;
import com.sba.lexilearnbe.modules.chat.dto.response.ConversationSummaryResponse;
import com.sba.lexilearnbe.modules.chat.dto.response.SendSyncMessageResponse;
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

    /** Gửi tin nhắn tới model đơn (only-llm/base-llm) -> trả JSON đồng bộ, chỉ lưu transcript. */
    SendSyncMessageResponse sendMessageSync(UUID accountId, SendSyncMessageRequest request);

    /** Dừng luồng stream đang chạy của 1 đoạn: huỷ AI + đóng kết nối, KHÔNG lưu câu trả lời. */
    void stopStream(UUID accountId, UUID conversationId);

    /** Người học xin tham gia tranh luận cùng hội đồng ở lượt đang chạy. */
    void debateOptin(UUID accountId, UUID conversationId);

    /** Chuyển 1 lượt phát biểu của người học xuống hội đồng đang chờ. */
    void debateReply(UUID accountId, UUID conversationId, DebateReplyRequest request);
}
