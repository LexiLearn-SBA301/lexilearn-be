package com.sba.lexilearnbe.modules.work.event;

import java.util.UUID;

public record AuthorSyncRequestedEvent(
        AuthorSyncAction action,
        UUID authorId,
        String authorSlug
) {

    public static AuthorSyncRequestedEvent upsert(UUID authorId) {
        return new AuthorSyncRequestedEvent(AuthorSyncAction.UPSERT, authorId, null);
    }

    public static AuthorSyncRequestedEvent delete(String authorSlug) {
        return new AuthorSyncRequestedEvent(AuthorSyncAction.DELETE, null, authorSlug);
    }
}
