package store.shportfolio.deploy.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.shportfolio.deploy.application.dto.StorageInfo;
import store.shportfolio.deploy.application.exception.StorageNotFoundException;
import store.shportfolio.deploy.application.ports.output.repository.StorageRepository;
import store.shportfolio.deploy.application.ports.output.s3.S3Bucket;
import store.shportfolio.deploy.domain.DeployDomainService;
import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.StorageName;
import store.shportfolio.deploy.domain.valueobject.StorageUrl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class StorageHandler {

    private final StorageRepository storageRepository;
    private final S3Bucket s3Bucket;
    private final DeployDomainService deployDomainService;

    public StorageHandler(StorageRepository storageRepository, S3Bucket s3Bucket,
                          DeployDomainService deployDomainService) {
        this.storageRepository = storageRepository;
        this.s3Bucket = s3Bucket;
        this.deployDomainService = deployDomainService;
    }

    public Storage createStorage(WebApp webApp) {
        return deployDomainService.createStorage(webApp.getId());
    }

    public void saveStorage(Storage storage) {
        storageRepository.save(storage);
    }

    @Transactional
    public Storage uploadS3(UUID applicationId, File file) {
        Storage storage = storageRepository.findByApplicationId(applicationId).orElseThrow(() ->
                new StorageNotFoundException("storage not found by id: " + applicationId));

        StorageInfo storageInfo = s3Bucket.uploadS3(file);

        log.info("storageName info: " + storageInfo.getStorageName());
        removeLocalFile(file);

        deployDomainService.saveStorageInfo(storage, new StorageName(storageInfo.getStorageName()),
                new StorageUrl(storageInfo.getFildUrl()));
        Storage saved = storageRepository.save(storage);
        log.info("Upload S3 successfully saved filename: {}, url: {}", saved.getStorageName(), saved.getStorageUrl());
        return saved;
    }

    @Transactional
    public void deleteStorage(UUID applicationId) {
        Storage storage = storageRepository.findByApplicationId(applicationId).orElseThrow(() ->
                new StorageNotFoundException("storage not found by id: " + applicationId));
        s3Bucket.deleteS3(storage.getStorageName().getValue());
        storageRepository.removeByApplicationId(applicationId);
    }

    private void removeLocalFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("Temporary file deleted: {}", targetFile.getAbsolutePath());
        } else {
            log.warn("Failed to delete temporary file: {}", targetFile.getAbsolutePath());
        }
    }
}
