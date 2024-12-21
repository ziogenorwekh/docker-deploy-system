package store.shportfolio.deploy.infrastructure.s3.adapter;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import store.shportfolio.deploy.application.ports.output.s3.S3Bucket;
import store.shportfolio.deploy.application.vo.StorageInfo;
import store.shportfolio.deploy.infrastructure.s3.exception.S3Exception;

import java.io.File;
import java.io.IOException;

@Slf4j
@Component
public class S3BuckImpl implements S3Bucket {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public S3BuckImpl(AmazonS3 amazonS3) {
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

        this.removeLocalFile(file);

        return storageInfo;
    }

    @Override
    public void deleteS3(String storageName) {
        try {
            amazonS3.deleteObject(bucket, storageName);
            log.trace("storage remove successful filename is : {}", storageName);
        } catch (Exception e) {
            throw new S3Exception(e.getMessage());
        }
    }

    private void upload(File file) {
        try {
            amazonS3.putObject(new PutObjectRequest(bucket, file.getName(), file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (Exception e) {
            this.removeLocalFile(file);
            throw new S3Exception(e.getMessage());
        }
    }

    private void removeLocalFile(File targetFile) {
        targetFile.delete();
    }

    private void validateFileType(File file) {
        if (file == null || !file.getName().toLowerCase().endsWith(".jar")) {
            throw new S3Exception("File type not supported");
        }
    }

}
