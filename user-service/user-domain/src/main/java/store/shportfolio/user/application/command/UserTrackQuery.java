package store.shportfolio.user.application.command;

import java.util.UUID;

public class UserTrackQuery {

    private final UUID userId;

    public UserTrackQuery(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
