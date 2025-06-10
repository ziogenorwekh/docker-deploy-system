package store.shportfolio.database.application;

import jakarta.validation.Valid;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.application.command.DatabaseCreateCommand;
import store.shportfolio.database.application.command.DatabaseCreateResponse;
import store.shportfolio.database.application.command.DatabaseTrackQuery;
import store.shportfolio.database.application.command.DatabaseTrackResponse;

public interface DatabaseApplicationService {

    DatabaseCreateResponse createDatabase(@Valid DatabaseCreateCommand databaseCreateCommand, UserGlobal userGlobal);

    DatabaseTrackResponse trackQuery(@Valid DatabaseTrackQuery trackQuery);

    void deleteDatabase(UserGlobal userGlobal);
}
