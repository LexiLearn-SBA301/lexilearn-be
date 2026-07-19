package com.sba.lexilearnbe.modules.work.event;

import java.util.UUID;

public record WorkSyncRequestedEvent(
        WorkSyncAction action,
        UUID workId,
        String workSlug
) {

    public static WorkSyncRequestedEvent upsert(UUID workId) {
        return new WorkSyncRequestedEvent(WorkSyncAction.UPSERT, workId, null);
    }

    public static WorkSyncRequestedEvent delete(String workSlug) {
        return new WorkSyncRequestedEvent(WorkSyncAction.DELETE, null, workSlug);
    }
}
