package store.shportfolio.database.usecase.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.domain.DatabaseDomainServiceImpl;
import store.shportfolio.database.domain.entity.Database;
import store.shportfolio.database.usecase.DatabaseUseCase;
import store.shportfolio.database.usecase.DatabaseUseCaseImpl;
import store.shportfolio.database.usecase.command.DatabaseCreateCommand;
import store.shportfolio.database.usecase.command.DatabaseCreateResponse;
import store.shportfolio.database.usecase.command.DatabaseTrackQuery;
import store.shportfolio.database.usecase.command.DatabaseTrackResponse;
import store.shportfolio.database.usecase.config.DatabaseEndpointConfigData;
import store.shportfolio.database.usecase.exception.DatabaseAlreadyCreatedException;
import store.shportfolio.database.usecase.mapper.DatabaseDataMapper;
import store.shportfolio.database.usecase.ports.output.DatabaseRepositoryPort;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DatabaseUseCaseTest {

    private DatabaseUseCase databaseUseCase;

    private DatabaseDataMapper databaseDataMapper;

    @Mock
    private DatabaseRepositoryPort databaseRepository;

    private final String userId = "userId";
    private final String username = "testUsername";


    @BeforeEach
    public void setUp() {
        DatabaseEndpointConfigData databaseEndpointConfigData = new DatabaseEndpointConfigData();
        databaseEndpointConfigData.setEndpointUrl("created-url");
        databaseDataMapper = new DatabaseDataMapper();
        databaseUseCase = new DatabaseUseCaseImpl(
                databaseRepository, databaseDataMapper, new DatabaseDomainServiceImpl(), databaseEndpointConfigData
        );
    }

    @Test
    @DisplayName("데이터베이스 생성 성공 테스트")
    public void testCreateDatabase() {
        // given
        String accessUrl = "created-url";
        DatabaseCreateCommand databaseCreateCommand = new
                DatabaseCreateCommand("databasePassword");
        UserGlobal userGlobal = new UserGlobal(userId, username);
        Database database = Database.createDatabase(userGlobal, databaseCreateCommand.getDatabasePassword());
        Database settingUrlDatabase = database;
        settingUrlDatabase.createAccessUrl(accessUrl);

        Mockito.when(databaseRepository.save(Mockito.any(Database.class))).thenReturn(settingUrlDatabase);

        // when
        DatabaseCreateResponse createResponse = databaseUseCase.createDatabase(databaseCreateCommand, userGlobal);

        // then
        Assertions.assertNotNull(createResponse);
        Assertions.assertEquals("databasePassword", createResponse.getDatabasePassword());
        Assertions.assertEquals("created-url", createResponse.getAccessUrl());
    }

    @Test
    @DisplayName("데이터베이스 정보 조회 성공 테스트")
    public void testTrackDatabase() {

        // given
        String accessUrl = "tracked-url";
        String password = "trackedPassword";
        DatabaseTrackQuery databaseTrackQuery = new DatabaseTrackQuery(userId);

        UserGlobal userGlobal = new UserGlobal(userId, username);
        Database database = Database.createDatabase(userGlobal, password);
        database.createAccessUrl(accessUrl);

        Mockito.when(databaseRepository.findByUserId(userId)).thenReturn(Optional.of(database));
        // when
        DatabaseTrackResponse databaseTrackResponse = databaseUseCase.trackDatabase(databaseTrackQuery);
        // then

        Assertions.assertNotNull(databaseTrackResponse);
        Assertions.assertEquals(database.getDatabaseName().getValue(), databaseTrackResponse.getDatabaseName());
        Assertions.assertEquals(database.getAccessUrl(), databaseTrackResponse.getAccessUrl());
        Assertions.assertEquals(database.getDatabaseUsername().getValue(), databaseTrackResponse.getDatabaseUsername());
    }

    @Test
    @DisplayName("이미 존재하는 데이터베이스 생성 시 예외 발생 테스트")
    void testCreateDatabase_whenAlreadyExists_shouldThrowException() {

        // given
        UserGlobal userGlobal = new UserGlobal(userId, username);
        Database existingDatabase = Database.createDatabase(userGlobal, "password");

        Mockito.when(databaseRepository.findByUserId(userId)).thenReturn(Optional.of(existingDatabase));

        DatabaseCreateCommand cmd = new DatabaseCreateCommand("password");

        // when
        DatabaseAlreadyCreatedException databaseAlreadyCreatedException = Assertions
                .assertThrows(DatabaseAlreadyCreatedException.class,
                        () -> databaseUseCase.createDatabase(cmd, userGlobal));

        // then
        Assertions.assertNotNull(databaseAlreadyCreatedException);
        Assertions.assertEquals("User " + username + " already exists",
                databaseAlreadyCreatedException.getMessage());
    }

    @Test
    @DisplayName("데이터베이스 삭제 시 존재하면 삭제되는 테스트")
    void testDeleteDatabase_whenExists_shouldRemove() {

        // given
        UserGlobal userGlobal = new UserGlobal(userId, username);
        Database db = Database.createDatabase(userGlobal, "pwd12345");

        Mockito.when(databaseRepository.findByUserId(userId)).thenReturn(Optional.of(db));

        // when
        databaseUseCase.deleteDatabase(userGlobal);

        // then
        Mockito.verify(databaseRepository, Mockito.times(1)).remove(db);
    }

    @Test
    @DisplayName("데이터베이스 삭제 시 존재하지 않으면 삭제되지 않는 테스트")
    void testDeleteDatabase_whenNotExists_shouldNotRemove() {

        // given
        UserGlobal userGlobal = new UserGlobal(userId, username);
        Mockito.when(databaseRepository.findByUserId(userId)).thenReturn(Optional.empty());

        // when
        databaseUseCase.deleteDatabase(userGlobal);

        // then
        Mockito.verify(databaseRepository, Mockito.never()).remove(Mockito.any());
    }

}
