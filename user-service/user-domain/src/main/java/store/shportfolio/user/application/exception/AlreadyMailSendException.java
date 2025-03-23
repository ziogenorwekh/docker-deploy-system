package store.shportfolio.user.application.exception;

public class AlreadyMailSendException extends RuntimeException {
    public AlreadyMailSendException(String message) {
        super(message);
    }

    public AlreadyMailSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
