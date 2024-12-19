package store.shportfolio.database.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.application.command.DatabaseCreateCommand;
import store.shportfolio.database.application.command.DatabaseCreateResponse;
import store.shportfolio.database.application.command.DatabaseTrackQuery;
import store.shportfolio.database.application.command.DatabaseTrackResponse;
import store.shportfolio.database.application.config.DatabaseEndpointConfigData;
import store.shportfolio.database.application.exception.DatabaseNotFoundException;
import store.shportfolio.database.application.mapper.DatabaseDataMapper;
import store.shportfolio.database.application.ports.output.DatabaseRepository;
import store.shportfolio.database.domain.DatabaseDomainService;
import store.shportfolio.database.domain.entity.Database;

@Slf4j
@Service
@Validated
public class DatabaseApplicationServiceImpl implements DatabaseApplicationService {

    private final DatabaseRepository databaseRepository;
    private final DatabaseDataMapper databaseDataMapper;
    private final DatabaseDomainService databaseDomainService;
    private DatabaseEndpointConfigData databaseEndpointConfigData;

    public DatabaseApplicationServiceImpl(DatabaseRepository databaseRepository,
                                          DatabaseDataMapper databaseDataMapper,
                                          DatabaseDomainService databaseDomainService,
                                          DatabaseEndpointConfigData databaseEndpointConfigData) {
        this.databaseRepository = databaseRepository;
        this.databaseDataMapper = databaseDataMapper;
        this.databaseDomainService = databaseDomainService;
        this.databaseEndpointConfigData = databaseEndpointConfigData;
    }

    @Override
    @Transactional
    public DatabaseCreateResponse createDatabase(DatabaseCreateCommand databaseCreateCommand, UserGlobal userGlobal) {

        Database database = databaseDomainService.createDatabase(userGlobal, databaseCreateCommand);

        String databaseAccessUrl = databaseEndpointConfigData.getEndpointUrl() + "/" + database.getDatabaseName();
        databaseDomainService.settingAccessUrl(database, databaseAccessUrl);
        log.info("database access url : {}", databaseAccessUrl);
        Database saved = databaseRepository.save(database);

        return databaseDataMapper.databaseToDatabaseCreateResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DatabaseTrackResponse trackQuery(DatabaseTrackQuery trackQuery) {
        Database database = databaseRepository.findByUserId(trackQuery.getUserId()).orElseThrow(() -> {
            throw new DatabaseNotFoundException("User not register user's database");
        });
        return databaseDataMapper.databaseToDatabaseTrackResponse(database);
    }

    @Override
    @Transactional
    public void deleteDatabase(UserGlobal userGlobal) {
        Database database = databaseRepository.findByUserId(userGlobal.getUserId()).orElseThrow(() -> {
            throw new DatabaseNotFoundException("User not register user's database");
        });
        databaseRepository.remove(database);
    }
}
