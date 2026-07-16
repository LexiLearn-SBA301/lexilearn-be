package com.sba.lexilearnbe.modules.chat.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sba.lexilearnbe.modules.auth.repository.AccountRepository;
import com.sba.lexilearnbe.modules.chat.client.AiChatClient;
import com.sba.lexilearnbe.modules.chat.dto.request.SendMessageRequest;
import com.sba.lexilearnbe.modules.chat.entity.ChatMessage;
import com.sba.lexilearnbe.modules.chat.entity.Conversation;
import com.sba.lexilearnbe.modules.chat.enums.MessageRole;
import com.sba.lexilearnbe.modules.chat.repository.ChatMessageRepository;
import com.sba.lexilearnbe.modules.chat.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Chạy NỀN việc chat: resolve/tạo conversation, lưu message, seed AI, relay stream từ AI xuống
 * FE và bắt câu trả lời cuối để lưu. Tách khỏi ChatServiceImpl để @Async hoạt động (proxy chỉ áp
 * dụng khi gọi CHÉO bean). Không mở transaction dài quanh cả stream — mỗi thao tác DB tự đủ
 * (repository CRUD đã transactional), tránh giữ kết nối DB suốt lúc AI đang sinh câu trả lời.
 */
@Component
@RequiredArgsConstructor
public class ChatRelayWorker {

    private static final Logger log = LoggerFactory.getLogger(ChatRelayWorker.class);
    private static final int TITLE_MAX = 60;

    private final AccountRepository accountRepository;
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final AiChatClient aiChatClient;
    private final ObjectMapper objectMapper;

    @Async
    public void relay(UUID accountId, SendMessageRequest request, SseEmitter emitter) {
        try {
            Conversation conversation = resolveOrCreate(accountId, request);
            // Event đầu tiên: báo FE conversationId THỰC (có thể vừa tạo mới) để FE lưu & chat tiếp.
            sendEvent(emitter, Map.of(
                    "type", "conversation",
                    "conversationId", conversation.getId().toString()));

            saveMessage(conversation, MessageRole.USER, request.message());

            // Đảm bảo AI có lịch sử: nguội (mới / hết TTL) thì seed lại từ DB.
            if (!aiChatClient.threadExists(conversation.getId())) {
                seedAi(conversation.getId());
            }

            String answer = aiChatClient.stream(conversation.getId(), request.message(), raw -> {
                try {
                    emitter.send(SseEmitter.event().data(raw));   // chuyền tay realtime xuống FE
                } catch (IOException io) {
                    throw new UncheckedIOException(io);           // FE đóng kết nối -> dừng relay
                }
            });

            if (answer != null && !answer.isBlank()) {
                saveMessage(conversation, MessageRole.ASSISTANT, answer);
                conversationRepository.touchUpdatedAt(conversation.getId(), LocalDateTime.now());
            }
            emitter.complete();
        } catch (Exception e) {
            log.warn("Relay chat lỗi account={}: {}", accountId, e.toString());
            try {
                sendEvent(emitter, Map.of(
                        "type", "error",
                        "content", "Có lỗi khi xử lý câu hỏi. Vui lòng thử lại."));
            } catch (Exception ignore) {
                // FE đã ngắt -> khỏi báo
            }
            emitter.complete();
        }
    }

    /** Nạp 10 lượt gần nhất (cũ -> mới) của đoạn vào checkpoint AI. */
    public void seedAi(UUID conversationId) {
        aiChatClient.seed(conversationId, buildSeedHistory(conversationId));
    }

    private List<Map<String, String>> buildSeedHistory(UUID conversationId) {
        List<ChatMessage> recent = chatMessageRepository.findTop10ByConversationIdOrderByCreatedAtDesc(conversationId);
        List<Map<String, String>> history = new ArrayList<>(recent.size());
        for (int i = recent.size() - 1; i >= 0; i--) {   // đảo mới->cũ thành cũ->mới
            ChatMessage m = recent.get(i);
            history.add(Map.of("role", m.getRole().toAiRole(), "content", m.getContent()));
        }
        return history;
    }

    private Conversation resolveOrCreate(UUID accountId, SendMessageRequest request) {
        if (request.conversationId() != null) {
            Optional<Conversation> existing =
                    conversationRepository.findByIdAndAccountId(request.conversationId(), accountId);
            if (existing.isPresent()) {
                return existing.get();
            }
            // id gửi lên không thuộc user / đã hết -> tạo đoạn mới (lazy-create).
        }
        Conversation created = Conversation.builder()
                .account(accountRepository.getReferenceById(accountId))   // proxy, không load full account
                .title(buildTitle(request.message()))
                .build();
        return conversationRepository.save(created);
    }

    private void saveMessage(Conversation conversation, MessageRole role, String content) {
        chatMessageRepository.save(ChatMessage.builder()
                .conversation(conversation)
                .role(role)
                .content(content)
                .build());
    }

    private String buildTitle(String message) {
        String trimmed = message.strip();
        return trimmed.length() <= TITLE_MAX ? trimmed : trimmed.substring(0, TITLE_MAX) + "…";
    }

    private void sendEvent(SseEmitter emitter, Map<String, String> payload) throws IOException {
        emitter.send(SseEmitter.event().data(objectMapper.writeValueAsString(payload)));
    }
}
