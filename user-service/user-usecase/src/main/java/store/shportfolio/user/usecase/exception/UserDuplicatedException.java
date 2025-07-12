package store.shportfolio.user.usecase.exception;

public class UserDuplicatedException extends RuntimeException {
    public UserDuplicatedException(String message) {
        super(message);
    }

    public UserDuplicatedException(String message, Throwable cause) {
        super(message, cause);
    }
}
