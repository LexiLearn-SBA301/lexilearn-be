package com.sba.lexilearnbe.shared.infrastructure.storage;

public interface ImageStorageService {

    ImageUploadSignature createUploadSignature(ImageUploadTarget target);

    StoredImage verifyUploadedImage(UploadedImageRequest uploadedImage, ImageUploadTarget target);

    void delete(String publicId);
}
