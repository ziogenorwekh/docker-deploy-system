package store.shportfolio.deploy.application.exception;

public class DockerContainerErrorException extends RuntimeException {
    public DockerContainerErrorException(String message) {
        super(message);
    }
}
