package store.shportfolio.database.usecase.ports.output;

import store.shportfolio.database.domain.entity.Database;

import java.util.List;
import java.util.Optional;

public interface DatabaseRepositoryPort {

    Optional<Database> findByUserId(String userId);

    Optional<Database> findByDatabaseName(String databaseName);

    Optional<Database> findByUserIdAndDatabaseName(String userId, String databaseName);

    List<Database> findAllByUserId(String userId);

    Database save(Database database);

    void remove(Database database);
}
