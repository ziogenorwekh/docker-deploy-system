package store.shportfolio.database.application.ports.output;

import store.shportfolio.database.domain.entity.Database;

import java.util.Optional;

public interface DatabaseRepository {

    Optional<Database> findByUserId(String userId);

    Database save(Database database);

    void remove(Database database);
}
