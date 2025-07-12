package store.shportfolio.user.usecase.exception;

public class CustomServiceUnavailableException extends RuntimeException {
    public CustomServiceUnavailableException(String message) {
        super(message);
    }
}
