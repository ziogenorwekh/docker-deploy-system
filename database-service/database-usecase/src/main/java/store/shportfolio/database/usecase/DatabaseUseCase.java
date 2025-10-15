package store.shportfolio.database.usecase;

import jakarta.validation.Valid;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.usecase.command.*;

import java.util.List;

public interface DatabaseUseCase {

    DatabaseCreateResponse createDatabase(@Valid DatabaseCreateCommand databaseCreateCommand,
                                          UserGlobal userGlobal);

    DatabaseTrackResponse trackDatabase(@Valid DatabaseOneTrackQuery trackQuery);

    List<DatabaseTrackResponse> trackDatabases(@Valid DatabaseAllTrackQuery trackQuery);

    void deleteDatabase(@Valid UserGlobal userGlobal, @Valid DatabaseDeleteCommand databaseDeleteCommand);

    void deleteAllDatabases(@Valid UserGlobal userGlobal);
}
