package store.shportfolio.deploy.infrastructure.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import store.shportfolio.common.domain.valueobject.ApplicationId;
import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.domain.valueobject.StorageName;
import store.shportfolio.deploy.domain.valueobject.StorageUrl;
import store.shportfolio.deploy.infrastructure.jpa.adapter.DockerContainerRepositoryImpl;
import store.shportfolio.deploy.infrastructure.jpa.adapter.StorageRepositoryImpl;
import store.shportfolio.deploy.infrastructure.jpa.adapter.WebAppRepositoryImpl;
import store.shportfolio.deploy.infrastructure.jpa.repository.StorageJpaRepository;
import store.shportfolio.deploy.infrastructure.jpa.repository.WebAppJpaRepository;

import java.util.Optional;
import java.util.UUID;

@ActiveProfiles("jpatest")
@DataJpaTest
@ContextConfiguration(classes = TestJpaConfig.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2,
        replace = AutoConfigureTestDatabase.Replace.ANY)
public class DeployJpaTests {

    private final UUID userId = UUID.randomUUID();

    @Autowired
    private WebAppJpaRepository webAppJpaRepository;

    @Autowired
    private WebAppRepositoryImpl webAppRepository;
    @Autowired
    private DockerContainerRepositoryImpl dockerContainerRepository;
    @Autowired
    private StorageRepositoryImpl storageRepository;
    @Autowired
    private StorageJpaRepository storageJpaRepository;

    @Test
    @DisplayName("create webapp entity test")
    public void createWebAppEntity() {
        // given
        String applicationName = "testApplication";
        int javaVersion = 17;
        int serverPort = 10010;
        WebApp webApp = WebApp.createWebApp(userId.toString(), applicationName,
                serverPort, javaVersion);

        // when
        WebApp saved = webAppRepository.save(webApp);
        Optional<WebApp> byApplicationId = webAppRepository.findByApplicationId(webApp.getId().getValue());

        // then
        Assertions.assertNotNull(saved);
        Assertions.assertEquals(webApp.getId(), saved.getId());
        Assertions.assertEquals(webApp.getCreatedAt(), saved.getCreatedAt());
        Assertions.assertTrue(byApplicationId.isPresent());
        Assertions.assertEquals(webApp.getId(), byApplicationId.get().getId());
    }

    @Test
    @DisplayName("save webapp entity test")
    public void saveWebAppEntity() {
        Storage storage = Storage.createStorage(new ApplicationId(UUID.randomUUID()));
        storage.savedStorage(new StorageUrl("url"), new StorageName("name"));
        Storage saved = storageRepository.save(storage);

        System.out.println(saved);
    }
}
