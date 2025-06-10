package store.shportfolio.deploy.application.exception;

public class DockerNotFoundException extends RuntimeException {
    public DockerNotFoundException(String message) {
        super(message);
    }
}
