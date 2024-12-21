package store.shportfolio.deploy.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import store.shportfolio.deploy.application.ports.output.repository.StorageRepository;
import store.shportfolio.deploy.application.ports.output.s3.S3Bucket;
import store.shportfolio.deploy.application.vo.StorageInfo;
import store.shportfolio.deploy.domain.DeployDomainService;
import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.ApplicationStatus;

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

    public Storage createAndSaveStorage(WebApp webApp) {
        return storageRepository.save(deployDomainService.createStorage(webApp.getId()));
    }

    public Storage uploadS3(WebApp webApp, MultipartFile file) throws IOException {
        Storage storage = webApp.getStorage();
        File multipartFileToFile = this.convertMultipartFileToFile(file);
        StorageInfo storageInfo = s3Bucket.uploadS3(multipartFileToFile);
        deployDomainService.saveStorageInfo(storage, storageInfo.getStorageName(), storageInfo.getFildUrl());
        deployDomainService.updateStorage(webApp, storage);
        Storage saved = storageRepository.save(storage);
        log.info("Upload S3 successfully saved filename: {}, url: {}", saved.getStorageName(), saved.getStorageUrl());
        return saved;
    }

    public void deleteStorage(WebApp webApp) {
        storageRepository.remove(webApp.getStorage());
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        String filename = String.format("%s-%s", UUID.randomUUID(), multipartFile.getOriginalFilename());
        File convertedFile = new File(filename);

        if (convertedFile.createNewFile()) {
            FileOutputStream fileOutputStream = new FileOutputStream(convertedFile);
            fileOutputStream.write(multipartFile.getBytes());
            fileOutputStream.close();
        }
        return convertedFile;
    }
}
