package store.shportfolio.deploy.application.ports.output.repository;

import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;

import java.util.Optional;
import java.util.UUID;

public interface StorageRepository {

    Storage save(Storage storage);

    Optional<Storage> findByApplicationId(UUID applicationId);

    void remove(Storage storage);

    void removeByApplicationId(UUID applicationId);
}
