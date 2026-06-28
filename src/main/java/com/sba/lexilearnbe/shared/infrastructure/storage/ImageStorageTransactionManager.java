package com.sba.lexilearnbe.shared.infrastructure.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageStorageTransactionManager {

    private final ImageStorageService imageStorageService;

    public void scheduleCreate(String newPublicId) {
        register(null, newPublicId);
    }

    public void scheduleReplacement(String oldPublicId, String newPublicId) {
        register(oldPublicId, newPublicId);
    }

    public void scheduleDeletion(String oldPublicId) {
        register(oldPublicId, null);
    }

    private void register(String oldPublicId, String newPublicId) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            safelyDelete(oldPublicId);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                safelyDelete(oldPublicId);
            }

            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) {
                    safelyDelete(newPublicId);
                }
            }
        });
    }

    private void safelyDelete(String publicId) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }
        try {
            imageStorageService.delete(publicId);
        } catch (RuntimeException exception) {
            log.error("Không thể cleanup ảnh Cloudinary với publicId={}", publicId, exception);
        }
    }
}
