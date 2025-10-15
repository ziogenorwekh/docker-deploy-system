package store.shportfolio.database.usecase;

import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import store.shportfolio.common.domain.valueobject.UserGlobal;
import store.shportfolio.database.usecase.command.DatabaseCreateCommand;
import store.shportfolio.database.usecase.command.DatabaseCreateResponse;
import store.shportfolio.database.usecase.command.DatabaseTrackQuery;
import store.shportfolio.database.usecase.command.DatabaseTrackResponse;

import java.util.List;

public interface DatabaseUseCase {

    DatabaseCreateResponse createDatabase(@Valid DatabaseCreateCommand databaseCreateCommand,
                                          UserGlobal userGlobal);

    DatabaseTrackResponse trackDatabase(@Valid DatabaseTrackQuery trackQuery);

    @Transactional(readOnly = true)
    List<DatabaseTrackResponse> trackDatabases(@Valid DatabaseTrackQuery trackQuery);

    void deleteDatabase(@Valid UserGlobal userGlobal);
}
