package store.shportfolio.database.infrastructure.mapper;

import org.springframework.stereotype.Component;
import store.shportfolio.common.domain.valueobject.DatabaseId;
import store.shportfolio.common.domain.valueobject.UserId;
import store.shportfolio.database.domain.entity.Database;
import store.shportfolio.database.domain.valueobject.DatabaseName;
import store.shportfolio.database.domain.valueobject.DatabasePassword;
import store.shportfolio.database.domain.valueobject.DatabaseUsername;
import store.shportfolio.database.infrastructure.entity.DatabaseEntity;

import java.util.UUID;

@Component
public class DatabaseEntityDataAccessMapper {


    public Database databaseEntityToDatabase(DatabaseEntity databaseEntity) {
        return Database.builder()
                .databaseId(new DatabaseId(UUID.fromString(databaseEntity.getDatabaseId())))
                .userId(new UserId(databaseEntity.getUserId()))
                .databaseUsername(new DatabaseUsername(databaseEntity.getDatabaseUsername()))
                .databaseName(new DatabaseName(databaseEntity.getDatabaseName()))
                .databasePassword(new DatabasePassword(databaseEntity.getDatabasePassword()))
                .accessUrl(databaseEntity.getAccessUrl())
                .build();
    }
    
    public DatabaseEntity databaseToDatabaseEntity(Database database) {
        return DatabaseEntity.builder()
                .databaseId(database.getId().getValue().toString())
                .userId(database.getUserId().getValue())
                .databaseName(database.getDatabaseName().getValue())
                .databasePassword(database.getDatabasePassword().getValue())
                .databaseUsername(database.getDatabaseUsername().getValue())
                .accessUrl(database.getAccessUrl())
                .build();
    }
}
