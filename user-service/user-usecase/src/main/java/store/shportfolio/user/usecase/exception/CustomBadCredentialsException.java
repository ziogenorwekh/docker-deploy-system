package store.shportfolio.user.usecase.exception;

public class CustomBadCredentialsException extends RuntimeException {
    public CustomBadCredentialsException(String message) {
        super(message);
    }
}
