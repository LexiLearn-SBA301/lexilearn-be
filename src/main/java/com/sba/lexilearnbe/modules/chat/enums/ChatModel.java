package com.sba.lexilearnbe.modules.chat.enums;

/**
 * Model đơn (không workflow, không stream) mà FE có thể chọn. Mỗi giá trị map tới 1 endpoint
 * RAG đồng bộ của AI service (rag-service).
 */
public enum ChatModel {
    ONLY_LLM("/chat/only-llm"),   // Mythis 5 — fine-tune văn học
    BASE_LLM("/chat/base-llm");   // HuKai 4.5 — model nền

    private final String aiPath;

    ChatModel(String aiPath) {
        this.aiPath = aiPath;
    }

    public String aiPath() {
        return aiPath;
    }
}
