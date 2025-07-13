package store.shportfolio.database.usecase;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.domain.DatabaseDomainService;
import store.shportfolio.database.domain.entity.Database;
import store.shportfolio.database.usecase.command.DatabaseCreateCommand;
import store.shportfolio.database.usecase.command.DatabaseCreateResponse;
import store.shportfolio.database.usecase.command.DatabaseTrackQuery;
import store.shportfolio.database.usecase.command.DatabaseTrackResponse;
import store.shportfolio.database.usecase.config.DatabaseEndpointConfigData;
import store.shportfolio.database.usecase.exception.DatabaseAlreadyCreatedException;
import store.shportfolio.database.usecase.exception.DatabaseNotFoundException;
import store.shportfolio.database.usecase.mapper.DatabaseDataMapper;
import store.shportfolio.database.usecase.ports.output.DatabaseRepositoryPort;

import java.util.Optional;

@Slf4j
@Service
@Validated
public class DatabaseUseCaseImpl implements DatabaseUseCase {

    private final DatabaseRepositoryPort databaseRepositoryPort;
    private final DatabaseDataMapper databaseDataMapper;
    private final DatabaseDomainService databaseDomainService;
    private final DatabaseEndpointConfigData databaseEndpointConfigData;

    public DatabaseUseCaseImpl(DatabaseRepositoryPort databaseRepositoryPort,
                               DatabaseDataMapper databaseDataMapper,
                               DatabaseDomainService databaseDomainService,
                               DatabaseEndpointConfigData databaseEndpointConfigData) {
        this.databaseRepositoryPort = databaseRepositoryPort;
        this.databaseDataMapper = databaseDataMapper;
        this.databaseDomainService = databaseDomainService;
        this.databaseEndpointConfigData = databaseEndpointConfigData;
    }

    @Override
    @Transactional
    public DatabaseCreateResponse createDatabase(@Valid DatabaseCreateCommand databaseCreateCommand,
                                                 @NotNull UserGlobal userGlobal) {
        isExistUsersDatabase(userGlobal);
        Database database = databaseDomainService.createDatabase(userGlobal,
                databaseCreateCommand.getDatabasePassword());

        String databaseAccessUrl = databaseEndpointConfigData.getEndpointUrl();
        databaseDomainService.settingAccessUrl(database, databaseAccessUrl);
        log.info("database access url : {}", databaseAccessUrl);
        Database saved = databaseRepositoryPort.save(database);

        return databaseDataMapper.databaseToDatabaseCreateResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DatabaseTrackResponse trackQuery(@Valid DatabaseTrackQuery trackQuery) {
        Database database = databaseRepositoryPort.findByUserId(trackQuery.getUserId())
                .orElseThrow(() -> new DatabaseNotFoundException("User not register user's database"));
        return databaseDataMapper.databaseToDatabaseTrackResponse(database);
    }

    @Override
    @Transactional
    public void deleteDatabase(@NotNull UserGlobal userGlobal) {
        Optional<Database> database = databaseRepositoryPort.findByUserId(userGlobal.getUserId());
        database.ifPresent(databaseRepositoryPort::remove);
    }

    private void isExistUsersDatabase(UserGlobal userGlobal) {
        databaseRepositoryPort.findByUserId(userGlobal.getUserId())
                .ifPresent(database -> {
                    throw new DatabaseAlreadyCreatedException(
                            String.format("User %s already exists", userGlobal.getUsername()));
                });
    }
}
