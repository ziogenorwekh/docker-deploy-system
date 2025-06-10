package store.shportfolio.user.application.exception;


public class GoogleException extends RuntimeException {
    public GoogleException(String message) {
        super(message);
    }

    public GoogleException(String message, Throwable cause) {
        super(message, cause);
    }
}
