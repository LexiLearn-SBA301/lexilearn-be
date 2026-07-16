-- Lịch sử chat của người dùng với trợ lý AI (LexiLearn).
-- conversations: mỗi đoạn hội thoại; id chính là thread_id gửi cho AI service.
-- chat_messages : từng lượt user/assistant trong đoạn.

CREATE TABLE conversations (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id  UUID NOT NULL,
    title       VARCHAR(255),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_conversations_account
        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);

-- Sidebar: liệt kê hội thoại của 1 user, mới nhất trước.
CREATE INDEX idx_conversations_account_updated
    ON conversations(account_id, updated_at DESC);

CREATE TABLE chat_messages (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    conversation_id UUID NOT NULL,
    role            VARCHAR(16) NOT NULL,
    content         TEXT NOT NULL,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_chat_messages_conversation
        FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    CONSTRAINT chk_chat_messages_role
        CHECK (role IN ('USER', 'ASSISTANT'))
);

-- Lấy transcript 1 đoạn theo thứ tự thời gian.
CREATE INDEX idx_chat_messages_conversation
    ON chat_messages(conversation_id, created_at);

CREATE TRIGGER tr_conversations_updated_at
    BEFORE UPDATE ON conversations
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER tr_chat_messages_updated_at
    BEFORE UPDATE ON chat_messages
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
