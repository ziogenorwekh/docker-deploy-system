package store.shportfolio.deploy.application.output.s3;

import store.shportfolio.deploy.application.dto.StorageInfo;

import java.io.File;

public interface S3Bucket {

    StorageInfo uploadS3(File file);

    void deleteS3(String storageName);
}
