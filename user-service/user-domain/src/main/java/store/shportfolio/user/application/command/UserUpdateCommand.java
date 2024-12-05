package store.shportfolio.user.application.command;

import java.util.UUID;

public class UserUpdateCommand {

    private final UUID userId;
    private final String currentPassword;
    private final String newPassword;

    public UserUpdateCommand(UUID userId, String currentPassword, String newPassword) {
        this.userId = userId;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
