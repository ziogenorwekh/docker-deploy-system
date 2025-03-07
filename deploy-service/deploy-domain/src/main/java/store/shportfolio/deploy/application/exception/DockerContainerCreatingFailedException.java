package store.shportfolio.deploy.application.exception;


// Not registered in controllerAdvice
public class DockerContainerCreatingFailedException extends RuntimeException {
    public DockerContainerCreatingFailedException(String message) {
        super(message);
    }
}
