package store.shportfolio.deploy.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import store.shportfolio.common.domain.entitiy.AggregateRoot;
import store.shportfolio.common.domain.valueobject.*;
import store.shportfolio.deploy.domain.exception.DomainException;
import store.shportfolio.deploy.domain.valueobject.ApplicationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@ToString
public class WebApp extends AggregateRoot<ApplicationId> {

    private final UserId userId;
    private final ApplicationName applicationName;
    private final ServerPort serverPort;
    private final JavaVersion javaVersion;
    private ApplicationStatus applicationStatus;
    private String errorMessages;
    private final LocalDateTime createdAt;

    @Builder
    public WebApp(ApplicationId applicationId, UserId userId,
                  ApplicationName applicationName,
                  ServerPort serverPort, JavaVersion javaVersion,
                  ApplicationStatus applicationStatus, String errorMessages, LocalDateTime createdAt) {
        super.setId(applicationId);
        this.userId = userId;
        this.applicationName = applicationName;
        this.serverPort = serverPort;
        this.javaVersion = javaVersion;
        this.applicationStatus = applicationStatus;
        this.errorMessages = errorMessages;
        this.createdAt = createdAt;
    }

    public static WebApp createWebApp(String userId, String applicationName,
                               int serverPort, int javaVersion) {
        ApplicationName.isValidApplicationName(applicationName);
        String lowerCase = ApplicationName.toLowerCase(applicationName);

        UserId nUserId = new UserId(userId);
        ApplicationName nApplicationName = new ApplicationName(lowerCase);
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
                .createdAt(LocalDateTime.now())
                .build();
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
