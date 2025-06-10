package store.shportfolio.deploy.application.exception;

public class DockerContainerException extends RuntimeException {
    public DockerContainerException(String message) {
        super(message);
    }

    public DockerContainerException(String message, Throwable cause) {
        super(message, cause);
    }
}
