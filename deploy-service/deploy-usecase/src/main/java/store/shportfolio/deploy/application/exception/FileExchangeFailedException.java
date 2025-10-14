package store.shportfolio.deploy.application.exception;

public class FileExchangeFailedException extends RuntimeException {
    public FileExchangeFailedException(String message) {
        super(message);
    }

    public FileExchangeFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
