package store.shportfolio.deploy.infrastructure.s3.adapter;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import store.shportfolio.deploy.application.exception.S3Exception;
import store.shportfolio.deploy.application.dto.StorageInfo;
import store.shportfolio.deploy.application.ports.output.s3.S3Bucket;

import java.io.File;

@Slf4j
@Component
public class S3BucketImpl implements S3Bucket {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public S3BucketImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public StorageInfo uploadS3(File file) {

        validateFileType(file);
        upload(file);

        StorageInfo storageInfo = StorageInfo.builder()
                .fildUrl(amazonS3.getUrl(bucket, file.getName()).toString())
                .storageName(file.getName())
                .build();
        log.info("Upload storage info to s3: url-> {} , name-> {}", storageInfo.getFildUrl(),
                storageInfo.getStorageName());

        return storageInfo;
    }

    @Override
    public void deleteS3(String storageName) {
        try {
            if (!amazonS3.doesObjectExist(bucket, storageName)) {
                log.info("Object does not exist in bucket. Filename: {}", storageName);
                return;
            }

            // 객체 삭제
            amazonS3.deleteObject(bucket, storageName);
            log.info("Storage removal successful. Filename: {}", storageName);
        } catch (Exception e) {
            throw new S3Exception("delete failed.", e);
        }
    }

    private void upload(File file) {
        try {
            amazonS3.putObject(new PutObjectRequest(bucket, file.getName(), file));
        } catch (Exception e) {
            log.error("upload failed: {}", e.getMessage());
            throw new S3Exception("upload failed.");
        }
    }


    private void validateFileType(File file) {
        if (file == null || !file.getName().toLowerCase().endsWith(".jar")) {
            throw new S3Exception("File type not supported");
        }
    }

}
