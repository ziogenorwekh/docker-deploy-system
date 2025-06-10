package store.shportfolio.deploy.application.exception;

public class WebAppUserNotMatchException extends RuntimeException {

    public WebAppUserNotMatchException(String message) {
        super(message);
    }

    public WebAppUserNotMatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
