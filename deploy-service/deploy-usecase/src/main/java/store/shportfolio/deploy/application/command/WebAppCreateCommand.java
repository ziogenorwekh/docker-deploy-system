package store.shportfolio.deploy.application.command;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebAppCreateCommand {
    @NotBlank
    @Size(min = 5, message = "Application name must be at least 5 characters long")
    @Pattern(
            regexp = "^[a-z]+(-[a-z]+)*$",
            message = "Application name must contain only lowercase letters and hyphens (-), and cannot start or end with a hyphen"
    )
    private String applicationName;


    @NotNull(message = "Java version must not be null")
    @Min(value = 17, message = "Java version must be at least 17")
    private Integer version;

    @NotNull(message = "Port number must not be null")
    @Min(value = 8000, message = "Port number must be at least 10000")
    private Integer port;
}
