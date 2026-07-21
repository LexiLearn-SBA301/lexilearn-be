package com.sba.lexilearnbe.modules.work.event;

import com.sba.lexilearnbe.modules.work.client.AiWorkSyncClient;
import com.sba.lexilearnbe.modules.work.dto.ai.AuthorSyncPayload;
import com.sba.lexilearnbe.modules.work.services.AuthorAiSyncSnapshotService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AuthorSyncEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuthorSyncEventListener.class);

    private final AuthorAiSyncSnapshotService snapshotService;
    private final AiWorkSyncClient aiWorkSyncClient;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(AuthorSyncRequestedEvent event) {
        try {
            if (event.action() == AuthorSyncAction.DELETE) {
                aiWorkSyncClient.deleteAuthor(event.authorSlug());
                return;
            }

            AuthorSyncPayload payload = snapshotService.buildSnapshot(event.authorId());
            aiWorkSyncClient.upsertAuthor(payload);
        } catch (Exception exception) {
            log.warn("Author AI sync listener failed action={} authorId={} authorSlug={}: {}",
                    event.action(), event.authorId(), event.authorSlug(), exception.getMessage());
        }
    }
}
