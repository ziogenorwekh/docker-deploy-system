package store.shportfolio.database.domain;

import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.application.command.DatabaseCreateCommand;
import store.shportfolio.database.domain.entity.Database;

public interface DatabaseDomainService {

    Database createDatabase(UserGlobal userGlobal, DatabaseCreateCommand databaseCreateCommand);
    void settingAccessUrl(Database database, String accessUrl);
}
