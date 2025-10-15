package store.shportfolio.database.usecase;

import jakarta.validation.Valid;
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
import store.shportfolio.database.usecase.exception.DatabaseNotFoundException;
import store.shportfolio.database.usecase.mapper.DatabaseDataMapper;
import store.shportfolio.database.usecase.ports.output.DatabaseRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                                                 UserGlobal userGlobal) {
        Database database;
        do {
            database = databaseDomainService.createDatabase(userGlobal,
                    databaseCreateCommand.getDatabasePassword());
        } while (isExistDatabaseName(database.getDatabaseName().getValue()));

        String databaseAccessUrl = databaseEndpointConfigData.getEndpointUrl();
        databaseDomainService.settingAccessUrl(database, databaseAccessUrl);
        log.info("database access url : {}", databaseAccessUrl);
        Database saved = databaseRepositoryPort.save(database);

        return databaseDataMapper.databaseToDatabaseCreateResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DatabaseTrackResponse trackDatabase(@Valid DatabaseTrackQuery trackQuery) {
        Database database = databaseRepositoryPort.findByUserIdAndDatabaseName(trackQuery.getUserId(),
                        trackQuery.getDatabaseName())
                .orElseThrow(() -> new DatabaseNotFoundException("User not register user's database"));
        return databaseDataMapper.databaseToDatabaseTrackResponse(database);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DatabaseTrackResponse> trackDatabases(@Valid DatabaseTrackQuery trackQuery) {
        List<Database> databases = databaseRepositoryPort.findAllByUserId(trackQuery.getUserId());
        if (databases.isEmpty()) {
            throw new DatabaseNotFoundException("User not register user's database");
        }
        return databases.stream().map(databaseDataMapper::databaseToDatabaseTrackResponse)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public void deleteDatabase(UserGlobal userGlobal) {
        Optional<Database> database = databaseRepositoryPort.findByUserId(userGlobal.getUserId());
        database.ifPresent(databaseRepositoryPort::remove);
    }

    private boolean isExistDatabaseName(String databaseName) {
        return databaseRepositoryPort.findByDatabaseName(databaseName).isPresent();
    }
}
