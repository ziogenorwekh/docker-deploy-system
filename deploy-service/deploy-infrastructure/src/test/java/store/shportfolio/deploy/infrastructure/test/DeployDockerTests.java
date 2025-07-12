package store.shportfolio.deploy.infrastructure.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import store.shportfolio.deploy.application.dto.DockerCreated;
import store.shportfolio.deploy.application.dto.ResourceUsage;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.DockerContainerStatus;
import store.shportfolio.deploy.infrastructure.docker.DockerConfig;
import store.shportfolio.deploy.infrastructure.docker.adapter.DockerConnectorImpl;

@ActiveProfiles("docker")
@ContextConfiguration(classes = {DockerConfig.class})
//@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.NEVER,
//        classes = {DeployDockerTests.class, DockerConnectorImpl.class,
//                DockerContainerHelper.class, DockerfileCreateHelper.class,
//                DockerImageCreateHelper.class, DockerResourceHelper.class,
//        })
public class DeployDockerTests {

//    @Autowired
    private DockerConnectorImpl dockerConnector;


//    @Test
    @DisplayName("create docker container test")
    public void testCreateDockerContainer() {
        WebApp webApp = WebApp.createWebApp("userId", "testApplication", 10350, 17);
        String fileUrl = "";

        DockerCreated container = dockerConnector.createContainer(webApp, fileUrl);

        Assertions.assertNotNull(container);

        System.out.println("container.getError() = " + container.getError());
        Assertions.assertEquals(DockerContainerStatus.STARTED, container.getDockerContainerStatus());
        Assertions.assertFalse(container.getDockerContainerId().isBlank());

        dockerConnector.dropContainer(container.getDockerContainerId());
        dockerConnector.removeImage(container.getDockerImageId());
    }

//    @Test
    @DisplayName("docker track logs test")
    public void allTests() throws InterruptedException {
        WebApp webApp = WebApp.createWebApp("userId", "testApplication", 10350, 17);
        String fileUrl = "successful";

        DockerCreated container = dockerConnector.createContainer(webApp, fileUrl);
        Assertions.assertNotNull(container);

        System.out.println("container.getDockerImageId() = " + container.getDockerImageId());
        System.out.println("container.getDockerContainerId() = " + container.getDockerContainerId());

        Thread.sleep(2000L);

        String trackLogs = dockerConnector.trackLogs(container.getDockerContainerId());
        Assertions.assertNotNull(trackLogs);


        ResourceUsage resourceUsage = dockerConnector.getResourceUsage(container.getDockerContainerId());
        Assertions.assertNotNull(resourceUsage);
        System.out.println("resourceUsage = " + resourceUsage);


        Boolean stopContainer = dockerConnector.stopContainer(container.getDockerContainerId());
        Assertions.assertNotNull(stopContainer);
        Assertions.assertTrue(stopContainer);

        Thread.sleep(4000L);

        Boolean startContainer = dockerConnector.startContainer(container.getDockerContainerId());
        Assertions.assertTrue(startContainer);

        Thread.sleep(4000L);

        dockerConnector.dropContainer(container.getDockerContainerId());
        dockerConnector.removeImage(container.getDockerImageId());
        Assertions.assertFalse(trackLogs.isEmpty());
        System.out.println("trackLogs = " + trackLogs);
    }
}
