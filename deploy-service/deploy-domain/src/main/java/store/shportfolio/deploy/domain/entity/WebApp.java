package store.shportfolio.deploy.domain.entity;

import lombok.Builder;
import lombok.Getter;
import store.shportfolio.common.domain.entitiy.AggregateRoot;
import store.shportfolio.common.domain.valueobject.*;
import store.shportfolio.deploy.domain.exception.DomainException;
import store.shportfolio.deploy.domain.valueobject.ApplicationStatus;

import java.util.UUID;

@Getter
public class WebApp extends AggregateRoot<ApplicationId> {

    private final UserId userId;
    private DockerContainer dockerContainer;
    private Storage storage;
    private final ApplicationName applicationName;
    private final ServerPort serverPort;
    private final JavaVersion javaVersion;
    private ApplicationStatus applicationStatus;
    private String errorMessages;

    @Builder
    public WebApp(ApplicationId applicationId, UserId userId,
                  Storage storage,
                  DockerContainer dockerContainer,
                  ApplicationName applicationName,
                  ServerPort serverPort, JavaVersion javaVersion,
                  ApplicationStatus applicationStatus, String errorMessages) {
        super.setId(applicationId);
        this.userId = userId;
        this.storage = storage;
        this.dockerContainer = dockerContainer;
        this.applicationName = applicationName;
        this.serverPort = serverPort;
        this.javaVersion = javaVersion;
        this.applicationStatus = applicationStatus;
        this.errorMessages = errorMessages;
    }

    public static WebApp createWebApp(String userId, String applicationName,
                               int serverPort, int javaVersion) {
        ApplicationName.isValidApplicationName(applicationName);
        UserId nUserId = new UserId(userId);
        ApplicationName nApplicationName = new ApplicationName(applicationName);
        ServerPort nServerPort = new ServerPort(serverPort);
        JavaVersion nJavaVersion = new JavaVersion(javaVersion);
        isValid(nServerPort,nJavaVersion);

        ApplicationId nApplicationId = new ApplicationId(UUID.randomUUID());
        return WebApp.builder()
                .applicationId(nApplicationId)
                .userId(nUserId)
                .applicationName(nApplicationName)
                .javaVersion(nJavaVersion)
                .applicationStatus(ApplicationStatus.CREATED)
                .serverPort(nServerPort)
                .build();
    }

    public void addDockerContainer(DockerContainer dockerContainer) {
        this.dockerContainer = dockerContainer;
    }

    public void addStorage(Storage storage) {
        this.storage = storage;
    }

    public void updateCreatedToContainerizing() {
        if (this.applicationStatus == ApplicationStatus.CREATED) {
            this.applicationStatus = ApplicationStatus.CONTAINERIZING;
        } else {
            throw new DomainException("Application status is not CREATED");
        }
    }

    public void updateContainerizingToCompleted() {
        if (this.applicationStatus == ApplicationStatus.CONTAINERIZING) {
            this.applicationStatus = ApplicationStatus.COMPLETE;
        } else {
            throw new DomainException("The application status is not CONTAINERIZING");
        }
    }

    public void failedApplication(String errorMessages) {
        this.applicationStatus = ApplicationStatus.FAILED;
        this.errorMessages = errorMessages;
    }


    private static void isValid(ServerPort serverPort, JavaVersion javaVersion) {
        if (!serverPort.isValid()) {
            throw new DomainException("Invalid server port " + serverPort);
        }
        if (!javaVersion.isValid()) {
            throw new DomainException("Invalid Java version " + javaVersion);
        }
    }
}
