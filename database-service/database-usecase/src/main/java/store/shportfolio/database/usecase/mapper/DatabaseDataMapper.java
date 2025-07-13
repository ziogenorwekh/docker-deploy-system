package store.shportfolio.database.usecase.mapper;

import org.springframework.stereotype.Component;
import store.shportfolio.database.domain.entity.Database;
import store.shportfolio.database.usecase.command.DatabaseCreateResponse;
import store.shportfolio.database.usecase.command.DatabaseTrackResponse;

@Component
public class DatabaseDataMapper {

    public DatabaseCreateResponse databaseToDatabaseCreateResponse(Database database) {
        return DatabaseCreateResponse.builder()
                .databaseName(database.getDatabaseName().getValue())
                .databaseUsername(database.getDatabaseUsername().getValue())
                .databasePassword(database.getDatabasePassword().getValue())
                .accessUrl(database.getAccessUrl())
                .build();
    }

    public DatabaseTrackResponse databaseToDatabaseTrackResponse(Database database) {
        return DatabaseTrackResponse.builder()
                .databaseName(database.getDatabaseName().getValue())
                .databaseUsername(database.getDatabaseUsername().getValue())
                .databasePassword(database.getDatabasePassword().getValue())
                .accessUrl(database.getAccessUrl())
                .build();
    }
}
