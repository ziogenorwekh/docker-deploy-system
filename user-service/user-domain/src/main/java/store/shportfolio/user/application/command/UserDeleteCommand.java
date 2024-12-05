package store.shportfolio.user.application.command;

import java.util.UUID;

public class UserDeleteCommand {

    private final UUID userId;

    public UserDeleteCommand(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
