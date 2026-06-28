package com.sba.lexilearnbe.shared.infrastructure.storage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UploadedImageRequest(
        @NotBlank(message = "Cloudinary publicId không được để trống")
        String publicId,

        @NotNull(message = "Cloudinary version không được để trống")
        @Positive(message = "Cloudinary version không hợp lệ")
        Long version,

        @NotBlank(message = "Cloudinary signature không được để trống")
        String signature
) {
}
