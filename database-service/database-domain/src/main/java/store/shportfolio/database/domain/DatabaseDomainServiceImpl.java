package store.shportfolio.database.domain;

import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.domain.entity.Database;


public class DatabaseDomainServiceImpl implements DatabaseDomainService {

    @Override
    public Database createDatabase(UserGlobal userGlobal, String password) {
        return Database.createDatabase(userGlobal, password);
    }

    @Override
    public void settingAccessUrl(Database database, String accessUrl) {
        database.createAccessUrl(accessUrl);
    }
}
