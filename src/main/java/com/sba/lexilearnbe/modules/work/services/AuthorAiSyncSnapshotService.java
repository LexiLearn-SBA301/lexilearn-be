package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.ai.AuthorSyncPayload;

import java.util.UUID;

public interface AuthorAiSyncSnapshotService {

    AuthorSyncPayload buildSnapshot(UUID authorId);
}
