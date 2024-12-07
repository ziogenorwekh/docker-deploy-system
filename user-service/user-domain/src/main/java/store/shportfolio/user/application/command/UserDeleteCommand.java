package store.shportfolio.user.application.command;

import java.util.UUID;

public class UserDeleteCommand {

    private final String userId;

    public UserDeleteCommand(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
