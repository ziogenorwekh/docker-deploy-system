package store.shportfolio.database.usecase.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DatabaseCreateResponse {

    private String databaseName;
    private String databaseUsername;
    private String databasePassword;
    private String accessUrl;

}
