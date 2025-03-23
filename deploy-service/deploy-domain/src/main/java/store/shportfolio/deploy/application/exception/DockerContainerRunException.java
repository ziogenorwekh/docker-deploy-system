package store.shportfolio.deploy.application.exception;

public class DockerContainerRunException extends RuntimeException {
    public DockerContainerRunException(String message) {
        super(message);
    }
}
