package store.shportfolio.deploy.application.ports.output.repository;

import store.shportfolio.deploy.domain.entity.Storage;

import java.util.Optional;
import java.util.UUID;

public interface StorageRepository {

    Storage save(Storage storage);

    Optional<Storage> findByStorageId(UUID storageId);

    void remove(Storage storage);
}
