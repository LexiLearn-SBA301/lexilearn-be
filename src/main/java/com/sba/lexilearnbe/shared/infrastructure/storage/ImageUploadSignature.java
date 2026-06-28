package com.sba.lexilearnbe.shared.infrastructure.storage;

public record ImageUploadSignature(
        String cloudName,
        String apiKey,
        String uploadUrl,
        long timestamp,
        String publicId,
        String allowedFormats,
        long maxFileSize,
        String signature
) {
}
