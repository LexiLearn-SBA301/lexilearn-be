package com.sba.lexilearnbe.modules.work.controller;

import com.sba.lexilearnbe.shared.common.response.ApiResponse;
import com.sba.lexilearnbe.shared.infrastructure.storage.ImageStorageService;
import com.sba.lexilearnbe.shared.infrastructure.storage.ImageUploadSignature;
import com.sba.lexilearnbe.shared.infrastructure.storage.ImageUploadSignatureRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/uploads/images")
@RequiredArgsConstructor
@Tag(name = "Image Upload", description = "Cấp chữ ký để ADMIN upload ảnh trực tiếp lên Cloudinary")
public class ImageUploadController {

    private final ImageStorageService imageStorageService;

    @PostMapping("/signature")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tạo chữ ký upload ảnh Cloudinary")
    public ResponseEntity<ApiResponse<ImageUploadSignature>> createUploadSignature(
            @Valid @RequestBody ImageUploadSignatureRequest request) {
        return ResponseEntity.ok(ApiResponse.<ImageUploadSignature>builder()
                .message("Tạo chữ ký upload ảnh thành công")
                .result(imageStorageService.createUploadSignature(request.target()))
                .build());
    }
}
