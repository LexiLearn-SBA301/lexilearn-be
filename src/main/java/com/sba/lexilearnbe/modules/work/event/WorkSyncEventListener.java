package com.sba.lexilearnbe.modules.work.event;

import com.sba.lexilearnbe.modules.work.client.AiWorkSyncClient;
import com.sba.lexilearnbe.modules.work.dto.ai.WorkSyncPayload;
import com.sba.lexilearnbe.modules.work.services.WorkAiSyncSnapshotService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class WorkSyncEventListener {

    private static final Logger log = LoggerFactory.getLogger(WorkSyncEventListener.class);

    private final WorkAiSyncSnapshotService snapshotService;
    private final AiWorkSyncClient aiWorkSyncClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WorkSyncRequestedEvent event) {
        try {
            if (event.action() == WorkSyncAction.DELETE) {
                aiWorkSyncClient.deleteWork(event.workSlug());
                return;
            }

            WorkSyncPayload payload = snapshotService.buildSnapshot(event.workId());
            aiWorkSyncClient.upsertWork(payload);
        } catch (Exception exception) {
            log.warn("Work AI sync listener failed action={} workId={} workSlug={}: {}",
                    event.action(), event.workId(), event.workSlug(), exception.getMessage());
        }
    }
}
