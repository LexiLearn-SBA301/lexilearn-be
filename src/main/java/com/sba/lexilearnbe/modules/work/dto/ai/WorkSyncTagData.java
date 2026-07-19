package com.sba.lexilearnbe.modules.work.dto.ai;

import java.util.UUID;

public record WorkSyncTagData(
        UUID id,
        String name,
        String slug,
        String description
) {
}
