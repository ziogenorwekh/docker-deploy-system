package store.shportfolio.deploy.infrastructure.jpa.adapter;

import org.springframework.stereotype.Repository;
import store.shportfolio.deploy.application.output.repository.StorageRepository;
import store.shportfolio.deploy.domain.entity.Storage;
import store.shportfolio.deploy.domain.entity.WebApp;
import store.shportfolio.deploy.infrastructure.jpa.entity.StorageEntity;
import store.shportfolio.deploy.infrastructure.jpa.mapper.DeployDataAccessMapper;
import store.shportfolio.deploy.infrastructure.jpa.repository.StorageJpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class StorageRepositoryImpl implements StorageRepository {

    private final StorageJpaRepository storageJpaRepository;
    private final DeployDataAccessMapper deployDataAccessMapper;

    public StorageRepositoryImpl(StorageJpaRepository storageJpaRepository,
                                 DeployDataAccessMapper deployDataAccessMapper) {
        this.storageJpaRepository = storageJpaRepository;
        this.deployDataAccessMapper = deployDataAccessMapper;
    }

    @Override
    public Storage save(Storage storage) {
        StorageEntity storageEntity = deployDataAccessMapper.storageEntityToStorage(storage);
        StorageEntity saved = storageJpaRepository.save(storageEntity);
        return deployDataAccessMapper.storageEntityToStorageEntity(saved);
    }

    @Override
    public Optional<Storage> findByApplicationId(UUID applicationId) {

        return storageJpaRepository.findByApplicationId(applicationId.toString())
                .map(deployDataAccessMapper::storageEntityToStorageEntity);
    }

    @Override
    public void remove(Storage storage) {
        storageJpaRepository.findByApplicationId(storage.getId().getValue().toString())
                .ifPresent(storageJpaRepository::delete);
    }

    @Override
    public void removeByApplicationId(UUID applicationId) {
        storageJpaRepository.removeByApplicationId(applicationId.toString());
    }
}
