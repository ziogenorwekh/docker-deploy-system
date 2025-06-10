package store.shportfolio.deploy.application.ports.output.s3;

import org.springframework.web.multipart.MultipartFile;
import store.shportfolio.deploy.application.vo.StorageInfo;

import java.io.File;
import java.io.IOException;

public interface S3Bucket {

    StorageInfo uploadS3(File file);

    void deleteS3(String storageName);
}
