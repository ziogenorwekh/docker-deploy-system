package store.shportfolio.deploy.infrastructure.test;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import store.shportfolio.deploy.application.dto.StorageInfo;
import store.shportfolio.deploy.infrastructure.s3.adapter.S3BucketImpl;
import store.shportfolio.deploy.infrastructure.s3.config.S3Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@ActiveProfiles("s3")
@ContextConfiguration(classes = {S3Config.class})
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.NEVER,
        classes = {DeployS3Tests.class, S3BucketImpl.class})
public class DeployS3Tests {

    @Autowired
    private S3BucketImpl s3Bucket;

    private File testFile;

    @BeforeEach
    void setUp() throws IOException {
        // .jar 확장자를 가진 더미 파일 생성
        testFile = File.createTempFile("test", ".jar");
        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("This is a test jar file.");
        }
    }

    @AfterEach
    void tearDown() {
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    @DisplayName("S3 file upload test")
    void testUploadS3() {
        // When
        StorageInfo storageInfo = s3Bucket.uploadS3(testFile);

        // Then
        Assertions.assertNotNull(storageInfo);
        Assertions.assertNotNull(storageInfo.getFildUrl());
        Assertions.assertEquals(testFile.getName(), storageInfo.getStorageName());

        //
        System.out.println("Uploaded file URL: " + storageInfo.getFildUrl());

        // finally
        s3Bucket.deleteS3(storageInfo.getStorageName());
    }

    @Test
    @DisplayName("S3 file delete test")
    void testDeleteS3() {
        // Given
        StorageInfo storageInfo = s3Bucket.uploadS3(testFile);

        // When
        s3Bucket.deleteS3(storageInfo.getStorageName());

        // Then
        System.out.println("File deleted: " + storageInfo.getStorageName());
    }
}
