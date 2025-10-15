package store.shportfolio.database.usecase.command;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class DatabaseOneTrackQuery {

    @NotNull(message = "User ID is required")
    private String userId;

    @NotNull(message = "Database name is required")
    private String databaseName;
}
