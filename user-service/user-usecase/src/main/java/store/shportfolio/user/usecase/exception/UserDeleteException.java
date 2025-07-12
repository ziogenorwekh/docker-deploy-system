package store.shportfolio.user.usecase.exception;

public class UserDeleteException extends RuntimeException {
    public UserDeleteException(String message) {
        super(message);
    }
}
