package store.shportfolio.database.infrastructure;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.domain.entity.Database;
import store.shportfolio.database.infrastructure.adapter.DatabaseRepositoryAdapter;
import store.shportfolio.database.infrastructure.config.JdbcConfiguration;
import store.shportfolio.database.infrastructure.config.JdbcDatabaseConfigData;
import store.shportfolio.database.infrastructure.config.JpaConfiguration;
import store.shportfolio.database.infrastructure.config.JpaDatabaseConfigData;
import store.shportfolio.database.infrastructure.entity.DatabaseEntity;
import store.shportfolio.database.infrastructure.mapper.DatabaseEntityDataAccessMapper;
import store.shportfolio.database.infrastructure.repository.DatabaseSchemaManagement;

import java.util.Optional;
import java.util.UUID;

@ActiveProfiles("test")
@EnableConfigurationProperties({JdbcDatabaseConfigData.class, JpaDatabaseConfigData.class})
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.NEVER, classes = {
        DatabaseRepositoryAdapter.class, JpaConfiguration.class, JdbcConfiguration.class,
        DatabaseEntityDataAccessMapper.class, DatabaseSchemaManagement.class, DatabaseEntity.class})
public class DatabaseRepositoryPortImplTest {

    @Autowired
    private DatabaseRepositoryAdapter databaseRepository;

//    @Test
    @DisplayName("create database test")
    public void testCreateDatabase() {

        // given
        String userId = UUID.randomUUID().toString();
        String username = "helloworld";
        UserGlobal userGlobal = new UserGlobal(userId, username);
        Database database = Database.createDatabase(userGlobal, "databasepwd1");
        database.createAccessUrl("accessurl");

        // when
        Database saved = databaseRepository.save(database);

        // then
        Assertions.assertNotNull(saved);

        // finally
        databaseRepository.remove(saved);
        Assertions.assertEquals(Optional.empty(),databaseRepository.findByUserId(userId));
    }


}
