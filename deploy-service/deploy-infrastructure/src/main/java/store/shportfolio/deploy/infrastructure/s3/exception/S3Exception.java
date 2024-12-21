package store.shportfolio.deploy.infrastructure.s3.exception;

public class S3Exception extends RuntimeException {
    public S3Exception(String message) {
        super(message);
    }
}
