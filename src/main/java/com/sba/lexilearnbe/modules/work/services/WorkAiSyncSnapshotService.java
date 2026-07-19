package com.sba.lexilearnbe.modules.work.services;

import com.sba.lexilearnbe.modules.work.dto.ai.WorkSyncPayload;

import java.util.UUID;

public interface WorkAiSyncSnapshotService {

    WorkSyncPayload buildSnapshot(UUID workId);
}
