package com.sba.lexilearnbe.modules.chat.repository;

import com.sba.lexilearnbe.modules.chat.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {

    /** Sidebar: các đoạn hội thoại của 1 user, mới cập nhật trước. */
    List<Conversation> findByAccountIdOrderByUpdatedAtDesc(UUID accountId);

    /** Lấy 1 đoạn CHỈ khi nó thuộc về user (chặn xem trộm đoạn người khác). */
    Optional<Conversation> findByIdAndAccountId(UUID id, UUID accountId);

    /** Đẩy updated_at để đoạn vừa chat nhảy lên đầu sidebar (không cần load entity). */
    @Transactional
    @Modifying
    @Query("UPDATE Conversation c SET c.updatedAt = :now WHERE c.id = :id")
    void touchUpdatedAt(@Param("id") UUID id, @Param("now") LocalDateTime now);
}
