package store.shportfolio.database.usecase.ports.output;

import store.shportfolio.database.domain.entity.Database;

import java.util.Optional;

public interface DatabaseRepositoryPort {

    Optional<Database> findByUserId(String userId);

    Database save(Database database);

    void remove(Database database);
}
