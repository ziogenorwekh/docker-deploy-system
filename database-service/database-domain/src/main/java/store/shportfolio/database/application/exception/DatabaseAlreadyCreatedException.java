package store.shportfolio.database.application.exception;

public class DatabaseAlreadyCreatedException extends RuntimeException {
    public DatabaseAlreadyCreatedException(String message) {
        super(message);
    }
}
