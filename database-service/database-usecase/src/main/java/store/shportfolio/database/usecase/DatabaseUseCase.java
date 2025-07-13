package store.shportfolio.database.usecase;

import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.usecase.command.DatabaseCreateCommand;
import store.shportfolio.database.usecase.command.DatabaseCreateResponse;
import store.shportfolio.database.usecase.command.DatabaseTrackQuery;
import store.shportfolio.database.usecase.command.DatabaseTrackResponse;

public interface DatabaseUseCase {

    DatabaseCreateResponse createDatabase(DatabaseCreateCommand databaseCreateCommand, UserGlobal userGlobal);

    DatabaseTrackResponse trackQuery(DatabaseTrackQuery trackQuery);

    void deleteDatabase(UserGlobal userGlobal);
}
