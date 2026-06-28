package com.sba.lexilearnbe.shared.infrastructure.storage;

import jakarta.validation.constraints.NotNull;

public record ImageUploadSignatureRequest(
        @NotNull(message = "Loại ảnh upload không được để trống")
        ImageUploadTarget target
) {
}
