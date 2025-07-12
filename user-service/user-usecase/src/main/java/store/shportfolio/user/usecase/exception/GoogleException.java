package store.shportfolio.user.usecase.exception;


public class GoogleException extends RuntimeException {
    public GoogleException(String message) {
        super(message);
    }

    public GoogleException(String message, Throwable cause) {
        super(message, cause);
    }
}
