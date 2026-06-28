package com.sba.lexilearnbe.shared.infrastructure.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudinaryImageStorageService implements ImageStorageService {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final String ALLOWED_FORMATS_PARAMETER = "jpg,jpeg,png,webp";
    private static final Set<String> ALLOWED_FORMATS = Set.of("jpg", "jpeg", "png", "webp");

    private final Cloudinary cloudinary;
    private final CloudinaryProperties properties;

    @Override
    public ImageUploadSignature createUploadSignature(ImageUploadTarget target) {
        requireConfiguration();

        long timestamp = Instant.now().getEpochSecond();
        String publicId = target.folder() + "/" + UUID.randomUUID();
        Map<String, Object> parameters = Map.of(
                "timestamp", timestamp,
                "public_id", publicId,
                "allowed_formats", ALLOWED_FORMATS_PARAMETER
        );

        try {
            String signature = cloudinary.apiSignRequest(
                    parameters,
                    properties.apiSecret(),
                    cloudinary.config.signatureVersion
            );
            return new ImageUploadSignature(
                    properties.cloudName(),
                    properties.apiKey(),
                    "https://api.cloudinary.com/v1_1/" + properties.cloudName() + "/image/upload",
                    timestamp,
                    publicId,
                    ALLOWED_FORMATS_PARAMETER,
                    MAX_FILE_SIZE,
                    signature
            );
        } catch (RuntimeException exception) {
            throw new ApiException(
                    ErrorCode.FAIL_TO_GENERATE_UPLOAD_SIGNATURE,
                    "Không thể tạo chữ ký upload Cloudinary"
            );
        }
    }

    @Override
    public StoredImage verifyUploadedImage(UploadedImageRequest uploadedImage, ImageUploadTarget target) {
        requireConfiguration();
        if (!target.owns(uploadedImage.publicId())) {
            throw invalidUploadedImage();
        }
        if (!cloudinary.verifyApiResponseSignature(
                uploadedImage.publicId(),
                uploadedImage.version().toString(),
                uploadedImage.signature())) {
            throw invalidUploadedImage();
        }

        try {
            ApiResponse resource = cloudinary.api().resource(
                    uploadedImage.publicId(),
                    ObjectUtils.asMap("resource_type", "image", "type", "upload")
            );
            validateResource(resource);
            return new StoredImage(
                    requiredResult(resource, "secure_url"),
                    requiredResult(resource, "public_id")
            );
        } catch (ApiException exception) {
            throw exception;
        } catch (Exception exception) {
            throw invalidUploadedImage();
        }
    }

    @Override
    public void delete(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }
        requireConfiguration();

        try {
            cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.asMap(
                            "resource_type", "image",
                            "invalidate", true
                    )
            );
        } catch (IOException | RuntimeException exception) {
            throw new ApiException(ErrorCode.FAIL_TO_UPLOAD, "Không thể xóa ảnh khỏi Cloudinary");
        }
    }

    private void requireConfiguration() {
        if (!properties.isConfigured()) {
            throw new ApiException(ErrorCode.FAIL_TO_UPLOAD, "Cloudinary chưa được cấu hình");
        }
    }

    private void validateResource(Map<?, ?> resource) {
        String format = requiredResult(resource, "format").toLowerCase(Locale.ROOT);
        Object bytesValue = resource.get("bytes");
        if (!(bytesValue instanceof Number bytes)
                || bytes.longValue() > MAX_FILE_SIZE
                || !ALLOWED_FORMATS.contains(format)) {
            deleteInvalidAsset(requiredResult(resource, "public_id"));
            throw new ApiException(
                    ErrorCode.INVALID_UPLOADED_IMAGE,
                    "Ảnh chỉ hỗ trợ JPEG, PNG, WebP và không được vượt quá 5 MB"
            );
        }
    }

    private void deleteInvalidAsset(String publicId) {
        try {
            delete(publicId);
        } catch (RuntimeException ignored) {
            // Validation vẫn phải thất bại dù cleanup Cloudinary tạm thời không thành công.
        }
    }

    private ApiException invalidUploadedImage() {
        return new ApiException(
                ErrorCode.INVALID_UPLOADED_IMAGE,
                "Ảnh Cloudinary không tồn tại hoặc chữ ký không hợp lệ"
        );
    }

    private String requiredResult(Map<?, ?> result, String key) {
        Object value = result.get(key);
        if (!(value instanceof String text) || text.isBlank()) {
            throw invalidUploadedImage();
        }
        return text;
    }
}
