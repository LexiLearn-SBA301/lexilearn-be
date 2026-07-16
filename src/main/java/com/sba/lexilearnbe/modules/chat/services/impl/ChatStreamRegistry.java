package com.sba.lexilearnbe.modules.chat.services.impl;

import com.sba.lexilearnbe.modules.chat.client.StreamSession;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Sổ các lượt stream ĐANG chạy, key theo conversationId -> /stop tra ra session để huỷ.
 * Giả định 1 conversation chỉ có 1 stream tại 1 thời điểm (UI khoá input khi đang trả lời).
 */
@Component
public class ChatStreamRegistry {

    // cuốn danh bạ chung tra cứu id để lấy session ra để stop
    private final Map<UUID, StreamSession> sessions = new ConcurrentHashMap<>();

    void register(UUID conversationId, StreamSession session) {
        sessions.put(conversationId, session);
    }

    /** Gỡ đúng session của mình (remove(key,value) -> không xoá nhầm session của lượt khác). */
    void unregister(UUID conversationId, StreamSession session) {
        sessions.remove(conversationId, session);
    }

    /** Huỷ lượt đang chạy của conversation (nếu có). Trả true nếu tìm thấy để huỷ. */
    public boolean stop(UUID conversationId) {
        StreamSession session = sessions.get(conversationId);
        if (session == null) {
            return false;
        }
        session.cancel();
        return true;
    }
}
