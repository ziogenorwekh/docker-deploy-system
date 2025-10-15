package store.shportfolio.database.infrastructure.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import store.shportfolio.database.domain.entity.Database;
import store.shportfolio.database.infrastructure.entity.DatabaseEntity;
import store.shportfolio.database.infrastructure.mapper.DatabaseEntityDataAccessMapper;
import store.shportfolio.database.infrastructure.repository.DatabaseJpaRepository;
import store.shportfolio.database.infrastructure.repository.DatabaseSchemaManagement;
import store.shportfolio.database.usecase.ports.output.DatabaseRepositoryPort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class DatabaseRepositoryAdapter implements DatabaseRepositoryPort {

    private final DatabaseJpaRepository jpaRepository;
    private final DatabaseSchemaManagement databaseSchemaManagement;
    private final DatabaseEntityDataAccessMapper databaseEntityDataAccessMapper;

    public DatabaseRepositoryAdapter(DatabaseJpaRepository jpaRepository,
                                     DatabaseSchemaManagement databaseSchemaManagement,
                                     DatabaseEntityDataAccessMapper databaseEntityDataAccessMapper) {
        this.jpaRepository = jpaRepository;
        this.databaseSchemaManagement = databaseSchemaManagement;
        this.databaseEntityDataAccessMapper = databaseEntityDataAccessMapper;
    }

    @Override
    public Optional<Database> findByUserId(String userId) {
        return jpaRepository.findByUserId(userId)
                .map(databaseEntityDataAccessMapper::databaseEntityToDatabase);
    }

    @Override
    public Optional<Database> findByDatabaseName(String databaseName) {
        return jpaRepository.findDatabaseEntityByDatabaseName(databaseName)
                .map(databaseEntityDataAccessMapper::databaseEntityToDatabase);
    }

    @Override
    public Optional<Database> findByUserIdAndDatabaseName(String userId, String databaseName) {
        return jpaRepository.findDatabaseEntityByUserIdAndDatabaseName(userId, databaseName)
                .map(databaseEntityDataAccessMapper::databaseEntityToDatabase);
    }

    @Override
    public List<Database> findAllByUserId(String userId) {
        return jpaRepository.findAllByUserId(userId).stream()
                .map(databaseEntityDataAccessMapper::databaseEntityToDatabase)
                .collect(Collectors.toList());
    }

    @Override
    public Database save(Database database) {

        databaseSchemaManagement.createSchema(database.getDatabaseName().getValue(),
                database.getDatabaseUsername().getValue(), database.getDatabasePassword().getValue());

        DatabaseEntity databaseEntity = databaseEntityDataAccessMapper
                .databaseToDatabaseEntity(database);
        DatabaseEntity savedDatabaseEntity = jpaRepository.save(databaseEntity);
        log.info("saved {} into {}", databaseEntity.getDatabaseId(), savedDatabaseEntity.getDatabaseName());
        return databaseEntityDataAccessMapper.databaseEntityToDatabase(savedDatabaseEntity);
    }

    @Override
    public void remove(Database database) {

        databaseSchemaManagement.dropSchema(database.getDatabaseName().getValue(),
                database.getDatabaseUsername().getValue());
        log.info("removing database {}", database.getDatabaseName().getValue());
        jpaRepository.deleteById(database.getId().getValue().toString());
    }
}
