package store.shportfolio.database.usecase.exception;

public class DatabaseAlreadyCreatedException extends RuntimeException {
    public DatabaseAlreadyCreatedException(String message) {
        super(message);
    }
}
