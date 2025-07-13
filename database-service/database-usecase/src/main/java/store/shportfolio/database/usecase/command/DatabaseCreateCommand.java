package store.shportfolio.database.usecase.command;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DatabaseCreateCommand {

    @NotEmpty(message = "Password must be necessary.")
    @Size(min = 8, message = "Password must be at least than 8 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Password must contain only letters and numbers.")
    private String databasePassword;

    @Builder
    public DatabaseCreateCommand(String databasePassword) {
        this.databasePassword = databasePassword;
    }
}
