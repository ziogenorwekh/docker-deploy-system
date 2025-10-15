package store.shportfolio.deploy.infrastructure.s3.adapter;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import store.shportfolio.deploy.application.exception.S3Exception;
import store.shportfolio.deploy.application.dto.StorageInfo;
import store.shportfolio.deploy.application.ports.output.s3.S3Bucket;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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
        try {
            // 파일 핸들을 확실히 닫기 위해 try-with-resources 사용
            // 스트림이 블록을 벗어나는 즉시 close()를 보장
            try (FileInputStream fileInputStream = new FileInputStream(file)) {

                // 메타데이터 설정
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.length());

                // PutObjectRequest에 File 대신 InputStream과 Metadata를 넘긴다.
                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        bucket,
                        file.getName(),
                        fileInputStream,
                        metadata
                );
                amazonS3.putObject(putObjectRequest);
            } catch (IOException e) {
                log.error("File stream operation failed: {}", e.getMessage());
                throw new S3Exception("File read error during S3 upload.", e);
            }

        } catch (S3Exception e) {
            log.error("upload failed: {}", e.getMessage());
            throw new S3Exception("upload failed.", e);
        }

        StorageInfo storageInfo = StorageInfo.builder()
                .fildUrl(amazonS3.getUrl(bucket, file.getName()).toString())
                .storageName(file.getName())
                .build();
        log.info("Storage upload successful. Filename: {}, URL: {}",
                storageInfo.getStorageName(), storageInfo.getFildUrl());
        return storageInfo;
    }


    @Override
    public void deleteS3(String storageName) {
        try {
            if (storageName.isEmpty() ||!amazonS3.doesObjectExist(bucket, storageName)) {
                log.info("Object does not exist in bucket. Filename: {}", storageName);
                return;
            }

            // 객체 삭제
            amazonS3.deleteObject(bucket, storageName);
            log.info("Storage removal successful. Filename: {}", storageName);
        } catch (S3Exception e) {
            log.error("Delete object failed: {}", e.getMessage());
            throw new S3Exception("delete failed.", e);
        }
    }


    private void validateFileType(File file) {
        if (file == null || !file.getName().toLowerCase().endsWith(".jar")) {
            log.error("Invalid file type: {}", file.getName());
            throw new S3Exception("File type not supported");
        }
    }

}
