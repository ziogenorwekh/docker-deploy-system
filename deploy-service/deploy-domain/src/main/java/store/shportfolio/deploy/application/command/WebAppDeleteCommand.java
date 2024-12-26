package store.shportfolio.deploy.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebAppDeleteCommand {
    private UUID applicationId;
}
