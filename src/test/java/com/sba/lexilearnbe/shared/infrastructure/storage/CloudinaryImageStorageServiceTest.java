package com.sba.lexilearnbe.shared.infrastructure.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.sba.lexilearnbe.shared.common.exception.ApiException;
import com.sba.lexilearnbe.shared.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CloudinaryImageStorageServiceTest {

    private static final CloudinaryProperties PROPERTIES =
            new CloudinaryProperties("test-cloud", "test-key", "test-secret");

    private Cloudinary cloudinary;
    private CloudinaryImageStorageService storageService;

    @BeforeEach
    void setUp() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", PROPERTIES.cloudName(),
                "api_key", PROPERTIES.apiKey(),
                "api_secret", PROPERTIES.apiSecret(),
                "secure", true
        ));
        storageService = new CloudinaryImageStorageService(cloudinary, PROPERTIES);
    }

    @Test
    void createsSignatureForExactUploadParameters() {
        ImageUploadSignature result =
                storageService.createUploadSignature(ImageUploadTarget.WORK_COVER);

        String expectedSignature = cloudinary.apiSignRequest(
                Map.of(
                        "timestamp", result.timestamp(),
                        "public_id", result.publicId(),
                        "allowed_formats", result.allowedFormats()
                ),
                PROPERTIES.apiSecret(),
                cloudinary.config.signatureVersion
        );

        assertTrue(result.publicId().startsWith("lexilearn/works/"));
        assertEquals(expectedSignature, result.signature());
        assertEquals(5L * 1024 * 1024, result.maxFileSize());
    }

    @Test
    void rejectsUploadResponseWithInvalidSignatureBeforeAssetLookup() {
        UploadedImageRequest uploadedImage = new UploadedImageRequest(
                "lexilearn/authors/image-id",
                123L,
                "invalid-signature"
        );

        ApiException exception = assertThrows(
                ApiException.class,
                () -> storageService.verifyUploadedImage(
                        uploadedImage,
                        ImageUploadTarget.AUTHOR_PORTRAIT
                )
        );

        assertEquals(ErrorCode.INVALID_UPLOADED_IMAGE, exception.getErrorCode());
    }

    @Test
    void rejectsAssetFromAnotherTargetFolder() {
        UploadedImageRequest uploadedImage = new UploadedImageRequest(
                "lexilearn/works/image-id",
                123L,
                "invalid-signature"
        );

        ApiException exception = assertThrows(
                ApiException.class,
                () -> storageService.verifyUploadedImage(
                        uploadedImage,
                        ImageUploadTarget.AUTHOR_PORTRAIT
                )
        );

        assertEquals(ErrorCode.INVALID_UPLOADED_IMAGE, exception.getErrorCode());
    }
}
