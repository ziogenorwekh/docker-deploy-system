package store.shportfolio.database.application;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.application.command.DatabaseCreateCommand;
import store.shportfolio.database.application.command.DatabaseCreateResponse;
import store.shportfolio.database.application.command.DatabaseTrackQuery;
import store.shportfolio.database.application.command.DatabaseTrackResponse;
import store.shportfolio.database.application.config.DatabaseEndpointConfigData;
import store.shportfolio.database.application.mapper.DatabaseDataMapper;
import store.shportfolio.database.application.ports.output.DatabaseRepository;
import store.shportfolio.database.domain.DatabaseDomainServiceImpl;
import store.shportfolio.database.domain.entity.Database;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseApplicationServiceTest {

    private DatabaseApplicationService databaseApplicationService;

    private DatabaseDataMapper databaseDataMapper;

    @Mock
    private DatabaseRepository databaseRepository;

    private final String userId = "userId";
    private final String username = "testUsername";


    @BeforeEach
    public void setUp() {
        DatabaseEndpointConfigData databaseEndpointConfigData = new DatabaseEndpointConfigData();
        databaseEndpointConfigData.setEndpointUrl("created-url");
        databaseDataMapper = new DatabaseDataMapper();
        databaseApplicationService = new DatabaseApplicationServiceImpl(
                databaseRepository, databaseDataMapper, new DatabaseDomainServiceImpl(),databaseEndpointConfigData
        );
    }

    @Test
    @DisplayName("create database method test")
    public void testCreateDatabase() {
        // given
        String accessUrl = "created-url";
        DatabaseCreateCommand databaseCreateCommand = new
                DatabaseCreateCommand("databasePassword");
        UserGlobal userGlobal = new UserGlobal(userId, username);
        Database database = Database.createDatabase(userGlobal, databaseCreateCommand);
        Database settingUrlDatabase = database;
        settingUrlDatabase.createAccessUrl(accessUrl);

        Mockito.when(databaseRepository.save(Mockito.any(Database.class))).thenReturn(settingUrlDatabase);

        // when
        DatabaseCreateResponse createResponse = databaseApplicationService.createDatabase(databaseCreateCommand, userGlobal);

        // then
        Assertions.assertNotNull(createResponse);
        Assertions.assertEquals("databasePassword",createResponse.getDatabasePassword());
        Assertions.assertEquals("created-url",createResponse.getAccessUrl());
    }

    @Test
    @DisplayName("track database info test")
    public void testTrackDatabase() {

        // given
        String accessUrl = "tracked-url";
        String password = "trackedPassword";
        DatabaseTrackQuery databaseTrackQuery = new DatabaseTrackQuery(userId);

        UserGlobal userGlobal = new UserGlobal(userId, username);
        Database database = Database.createDatabase(userGlobal, new DatabaseCreateCommand(password));
        database.createAccessUrl(accessUrl);

        Mockito.when(databaseRepository.findByUserId(userId)).thenReturn(Optional.of(database));
        // when
        DatabaseTrackResponse databaseTrackResponse = databaseApplicationService.trackQuery(databaseTrackQuery);
        // then

        Assertions.assertNotNull(databaseTrackResponse);
        Assertions.assertEquals(database.getDatabaseName().getValue(), databaseTrackResponse.getDatabaseName());
        Assertions.assertEquals(database.getAccessUrl(), databaseTrackResponse.getAccessUrl());
        Assertions.assertEquals(database.getDatabaseUsername().getValue(), databaseTrackResponse.getDatabaseUsername());
    }

}
