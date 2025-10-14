package store.shportfolio.deploy.application.exception;

public class WebAppException extends RuntimeException {
    public WebAppException(String message) {
        super(message);
    }
    public WebAppException(String message, Throwable cause) {
        super(message, cause);
    }
}
