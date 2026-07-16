package com.sba.lexilearnbe.modules.chat.services.impl;

import com.sba.lexilearnbe.modules.auth.repository.AccountRepository;
import com.sba.lexilearnbe.modules.chat.entity.ChatMessage;
import com.sba.lexilearnbe.modules.chat.entity.Conversation;
import com.sba.lexilearnbe.modules.chat.enums.MessageRole;
import com.sba.lexilearnbe.modules.chat.repository.ChatMessageRepository;
import com.sba.lexilearnbe.modules.chat.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Ghi conversation/message dùng chung cho cả luồng stream (ChatRelayWorker) lẫn luồng đồng bộ
 * (ChatServiceImpl). Mỗi thao tác là 1 lần gọi repository (tự đủ transaction) — cố ý KHÔNG mở
 * transaction dài để không giữ kết nối DB suốt lúc AI đang sinh câu trả lời.
 */
@Component
@RequiredArgsConstructor
public class ConversationWriter {

    private static final int TITLE_MAX = 60;

    private final AccountRepository accountRepository;
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;

    /** Có conversationId hợp lệ của user -> dùng lại; không thì lazy-create đoạn mới. */
    public Conversation resolveOrCreate(UUID accountId, UUID conversationId, String firstMessage) {
        if (conversationId != null) {
            Optional<Conversation> existing =
                    conversationRepository.findByIdAndAccountId(conversationId, accountId);
            if (existing.isPresent()) {
                return existing.get();
            }
            // id gửi lên không thuộc user / đã hết -> tạo đoạn mới (lazy-create).
        }
        Conversation created = Conversation.builder()
                .account(accountRepository.getReferenceById(accountId))   // proxy, không load full account
                .title(buildTitle(firstMessage))
                .build();
        return conversationRepository.save(created);
    }

    public void saveMessage(Conversation conversation, MessageRole role, String content) {
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
}
