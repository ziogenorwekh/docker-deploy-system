package store.shportfolio.deploy.application.exception;

public class DockerImageCreationException extends RuntimeException {
    public DockerImageCreationException(String message) {
        super(message);
    }
}
