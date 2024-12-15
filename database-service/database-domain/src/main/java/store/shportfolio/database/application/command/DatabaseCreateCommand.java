package store.shportfolio.database.application.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DatabaseCreateCommand {

    private String databasePassword;

    @Builder
    public DatabaseCreateCommand(String databasePassword) {
        this.databasePassword = databasePassword;
    }
}
