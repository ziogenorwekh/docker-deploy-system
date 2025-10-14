package store.shportfolio.database.usecase;

import jakarta.validation.Valid;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.usecase.command.DatabaseCreateCommand;
import store.shportfolio.database.usecase.command.DatabaseCreateResponse;
import store.shportfolio.database.usecase.command.DatabaseTrackQuery;
import store.shportfolio.database.usecase.command.DatabaseTrackResponse;

public interface DatabaseUseCase {

    DatabaseCreateResponse createDatabase(@Valid DatabaseCreateCommand databaseCreateCommand, UserGlobal userGlobal);

    DatabaseTrackResponse trackQuery(@Valid DatabaseTrackQuery trackQuery);

    void deleteDatabase(@Valid UserGlobal userGlobal);
}
