package com.sba.lexilearnbe.modules.chat.entity;

import com.sba.lexilearnbe.modules.auth.entity.Account;
import com.sba.lexilearnbe.shared.infrastructure.persistence.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Một đoạn hội thoại của người dùng với AI. id của entity chính là thread_id gửi cho AI
 * service (rag-service) -> AI checkpoint và DB dùng chung 1 định danh.
 */
@Entity
@Table(name = "conversations")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "title", length = 255)
    private String title;
}
