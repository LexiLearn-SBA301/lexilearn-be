package com.sba.lexilearnbe.shared.infrastructure.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImageStorageTransactionManagerTest {

    private final RecordingImageStorageService storageService = new RecordingImageStorageService();
    private final ImageStorageTransactionManager manager =
            new ImageStorageTransactionManager(storageService);

    @AfterEach
    void clearSynchronization() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    @Test
    void replacementDeletesOldImageOnlyAfterCommit() {
        TransactionSynchronizationManager.initSynchronization();

        manager.scheduleReplacement("old-image", "new-image");

        assertTrue(storageService.deletedPublicIds.isEmpty());
        completeTransaction(TransactionSynchronization.STATUS_COMMITTED);

        assertEquals(List.of("old-image"), storageService.deletedPublicIds);
    }

    @Test
    void replacementDeletesNewImageWhenTransactionRollsBack() {
        TransactionSynchronizationManager.initSynchronization();

        manager.scheduleReplacement("old-image", "new-image");

        completeTransaction(TransactionSynchronization.STATUS_ROLLED_BACK);

        assertEquals(List.of("new-image"), storageService.deletedPublicIds);
    }

    @Test
    void creationDeletesUploadedImageWhenTransactionRollsBack() {
        TransactionSynchronizationManager.initSynchronization();

        manager.scheduleCreate("new-image");

        completeTransaction(TransactionSynchronization.STATUS_ROLLED_BACK);

        assertEquals(List.of("new-image"), storageService.deletedPublicIds);
    }

    private void completeTransaction(int status) {
        List<TransactionSynchronization> synchronizations =
                TransactionSynchronizationManager.getSynchronizations();
        if (status == TransactionSynchronization.STATUS_COMMITTED) {
            synchronizations.forEach(TransactionSynchronization::afterCommit);
        }
        synchronizations.forEach(synchronization -> synchronization.afterCompletion(status));
    }

    private static class RecordingImageStorageService implements ImageStorageService {
        private final List<String> deletedPublicIds = new ArrayList<>();

        @Override
        public ImageUploadSignature createUploadSignature(ImageUploadTarget target) {
            throw new UnsupportedOperationException();
        }

        @Override
        public StoredImage verifyUploadedImage(
                UploadedImageRequest uploadedImage,
                ImageUploadTarget target) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void delete(String publicId) {
            deletedPublicIds.add(publicId);
        }
    }
}
