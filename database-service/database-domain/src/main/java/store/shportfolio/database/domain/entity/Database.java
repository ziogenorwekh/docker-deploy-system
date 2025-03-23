package store.shportfolio.database.domain.entity;

import lombok.Builder;
import lombok.Getter;
import store.shportfolio.common.domain.entitiy.AggregateRoot;
import store.shportfolio.common.domain.valueobject.DatabaseId;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.common.domain.valueobject.UserId;
import store.shportfolio.database.application.command.DatabaseCreateCommand;
import store.shportfolio.database.domain.exception.DomainException;
import store.shportfolio.database.domain.valueobject.DatabaseName;
import store.shportfolio.database.domain.valueobject.DatabasePassword;
import store.shportfolio.database.domain.valueobject.DatabaseUsername;

import java.util.UUID;

@Getter
public class Database extends AggregateRoot<DatabaseId> {

    private final UserId userId;
    private final DatabaseUsername databaseUsername;
    private final DatabaseName databaseName;
    private final DatabasePassword databasePassword;
    private String accessUrl;

    @Builder
    public Database(DatabaseId databaseId, UserId userId, DatabaseUsername databaseUsername,
                    DatabaseName databaseName, DatabasePassword databasePassword, String accessUrl) {
        super.setId(databaseId);
        this.userId = userId;
        this.databaseUsername = databaseUsername;
        this.databaseName = databaseName;
        this.databasePassword = databasePassword;
        this.accessUrl = accessUrl;
    }


    public static Database createDatabase(UserGlobal userGlobal, DatabaseCreateCommand databaseCreateCommand) {
        UUID databaseId = UUID.randomUUID();
        DatabaseId databaseIdObject = new DatabaseId(databaseId);
        String databasePassword = databaseCreateCommand.getDatabasePassword();
        DatabasePassword newPassword = new DatabasePassword(databasePassword);
        isValidDatabasePassword(newPassword);
        UserId newUserId = new UserId(userGlobal.getUserId());
        DatabaseUsername createdDatabaseUsername = DatabaseUsername.fromUsername(userGlobal.getUsername());
        DatabaseName createdDatabaseName = DatabaseName.fromUsername(userGlobal.getUsername());
        String newAccessUrl = "";
        return new Database(databaseIdObject, newUserId, createdDatabaseUsername, createdDatabaseName, newPassword, newAccessUrl);
    }

    public void createAccessUrl(String createAccessUrl) {
        if (!this.accessUrl.isEmpty()) {
            throw new DomainException("Access url already set");
        }
        this.accessUrl = createAccessUrl;
    }


    private static void isValidDatabasePassword(DatabasePassword databasePassword) {
        if (!databasePassword.isValid() || !databasePassword.atLeast8Characters()) {
            throw new DomainException("Database password is invalid or has at least 8 characters");
        }
    }

}
