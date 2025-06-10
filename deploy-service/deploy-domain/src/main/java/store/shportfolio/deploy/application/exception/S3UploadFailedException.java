package store.shportfolio.deploy.application.exception;

public class S3UploadFailedException extends RuntimeException {
    public S3UploadFailedException(String message) {
        super(message);
    }

    public S3UploadFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
