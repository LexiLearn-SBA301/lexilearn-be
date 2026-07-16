package com.sba.lexilearnbe.modules.chat.repository;

import com.sba.lexilearnbe.modules.chat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    /** Toàn bộ transcript 1 đoạn, cũ -> mới (để hiển thị lại). */
    List<ChatMessage> findByConversationIdOrderByCreatedAtAsc(UUID conversationId);

    /** 10 message gần nhất (mới -> cũ) để seed cho AI; caller đảo lại thành cũ -> mới. */
    List<ChatMessage> findTop10ByConversationIdOrderByCreatedAtDesc(UUID conversationId);
}
