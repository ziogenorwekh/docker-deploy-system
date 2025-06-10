package store.shportfolio.deploy.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebAppCreateCommand {
    private String applicationName;
    private int version;
    private int port;
}
