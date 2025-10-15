package store.shportfolio.deploy.infrastructure.test;

import com.github.dockerjava.api.DockerClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import store.shportfolio.deploy.infrastructure.docker.DockerConfig;
import store.shportfolio.deploy.infrastructure.docker.adapter.DockerConnectorImpl;
import store.shportfolio.deploy.infrastructure.docker.helper.DockerContainerHelper;
import store.shportfolio.deploy.infrastructure.docker.helper.DockerImageCreateHelper;
import store.shportfolio.deploy.infrastructure.docker.helper.DockerResourceHelper;
import store.shportfolio.deploy.infrastructure.docker.helper.DockerfileCreateHelper;

@Disabled("Requires Docker environment")
@ActiveProfiles("docker")
@ContextConfiguration(classes = {DockerConfig.class})
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.NEVER,
        classes = {DeployDockerTests.class, DockerConnectorImpl.class,
                DockerContainerHelper.class, DockerfileCreateHelper.class,
                DockerImageCreateHelper.class, DockerResourceHelper.class,
        })
public class DockerAccessTest {


    @Autowired
    private DockerClient dockerClient;

    @Test
    public void testDockerClient() {
        String version = dockerClient.versionCmd().exec().getVersion();
        System.out.println("Docker Version: " + version);
        dockerClient.pingCmd().exec();
    }
}
