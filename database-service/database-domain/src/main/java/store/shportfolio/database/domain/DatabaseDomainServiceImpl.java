package store.shportfolio.database.domain;

import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.application.command.DatabaseCreateCommand;
import store.shportfolio.database.domain.entity.Database;


public class DatabaseDomainServiceImpl implements DatabaseDomainService {

    @Override
    public Database createDatabase(UserGlobal userGlobal, DatabaseCreateCommand databaseCreateCommand) {
        return Database.createDatabase(userGlobal, databaseCreateCommand);
    }

    @Override
    public void settingAccessUrl(Database database, String accessUrl) {
        database.createAccessUrl(accessUrl);
    }
}
