package store.shportfolio.database.application.exception;

public class DatabaseNotFoundException extends RuntimeException {
    public DatabaseNotFoundException(String message) {
        super(message);
    }

    public DatabaseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
