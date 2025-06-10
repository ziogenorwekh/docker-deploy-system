package store.shportfolio.database.infrastructure.exception;

public class DatabaseSchemaException extends RuntimeException {
    public DatabaseSchemaException(String message) {
        super(message);
    }

    public DatabaseSchemaException(String message, Throwable cause) {
        super(message, cause);
    }
}
