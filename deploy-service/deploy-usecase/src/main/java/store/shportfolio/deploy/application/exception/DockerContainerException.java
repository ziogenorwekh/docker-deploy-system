package store.shportfolio.deploy.application.exception;

public class DockerContainerException extends WebAppException {
    public DockerContainerException(String message) {
        super(message);
    }

    public DockerContainerException(String message, Throwable cause) {
        super(message, cause);
    }
}
