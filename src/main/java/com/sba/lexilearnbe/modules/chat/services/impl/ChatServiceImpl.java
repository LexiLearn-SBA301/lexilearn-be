package com.sba.lexilearnbe.modules.chat.services.impl;

import com.sba.lexilearnbe.modules.chat.dto.request.SendMessageRequest;
import com.sba.lexilearnbe.modules.chat.dto.response.ChatMessageResponse;
import com.sba.lexilearnbe.modules.chat.dto.response.ConversationDetailResponse;
import com.sba.lexilearnbe.modules.chat.dto.response.ConversationSummaryResponse;
import com.sba.lexilearnbe.modules.chat.entity.Conversation;
import com.sba.lexilearnbe.modules.chat.mapper.ChatMapper;
import com.sba.lexilearnbe.modules.chat.repository.ChatMessageRepository;
import com.sba.lexilearnbe.modules.chat.repository.ConversationRepository;
import com.sba.lexilearnbe.modules.chat.services.ChatService;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    // Timeout của kết nối SSE (ms). Bài phân tích sâu có thể chạy khá lâu -> để rộng.
    private static final long SSE_TIMEOUT_MS = 5 * 60 * 1000L;

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMapper chatMapper;
    private final ChatRelayWorker relayWorker;

    @Override
    @Transactional(readOnly = true)
    public List<ConversationSummaryResponse> listConversations(UUID accountId) {
        return conversationRepository.findByAccountIdOrderByUpdatedAtDesc(accountId)
                .stream()
                .map(chatMapper::toSummary)
                .toList();
    }

    @Override
    public ConversationDetailResponse openConversation(UUID accountId, UUID conversationId) {
        Conversation conversation = conversationRepository.findByIdAndAccountId(conversationId, accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.CHAT_SESSION_NOT_FOUND));

        List<ChatMessageResponse> messages = chatMessageRepository
                .findByConversationIdOrderByCreatedAtAsc(conversationId)
                .stream()
                .map(chatMapper::toMessageResponse)
                .toList();

        // Nạp lịch sử cho AI (ngoài transaction) để checkpoint ấm, sẵn sàng chat tiếp.
        relayWorker.seedAi(conversationId);

        return new ConversationDetailResponse(conversation.getId(), conversation.getTitle(), messages);
    }

    @Override
    @Transactional
    public void deleteConversation(UUID accountId, UUID conversationId) {
        Conversation conversation = conversationRepository.findByIdAndAccountId(conversationId, accountId)
                .orElseThrow(() -> new ApiException(ErrorCode.CHAT_SESSION_NOT_FOUND));
        conversationRepository.delete(conversation);
    }

    @Override
    public SseEmitter streamMessage(UUID accountId, SendMessageRequest request) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        emitter.onError(err -> emitter.complete());
        relayWorker.relay(accountId, request, emitter);   // @Async -> chạy nền, trả emitter ngay
        return emitter;
    }
}
