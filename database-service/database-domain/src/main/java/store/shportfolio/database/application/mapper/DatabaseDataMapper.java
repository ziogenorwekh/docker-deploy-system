package store.shportfolio.database.application.mapper;

import org.springframework.stereotype.Component;
import store.shportfolio.database.application.command.DatabaseCreateResponse;
import store.shportfolio.database.application.command.DatabaseTrackResponse;
import store.shportfolio.database.domain.entity.Database;

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
