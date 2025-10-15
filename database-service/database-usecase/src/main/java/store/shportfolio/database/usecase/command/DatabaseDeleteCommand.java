package store.shportfolio.database.usecase.command;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DatabaseDeleteCommand {

    @NotNull(message = "databaseName is required")
    private String databaseName;
}
