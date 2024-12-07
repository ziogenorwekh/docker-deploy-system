package store.shportfolio.user.application.command;


public class UserUpdateCommand {

    private final String userId;
    private final String currentPassword;
    private final String newPassword;

    public UserUpdateCommand(String userId, String currentPassword, String newPassword) {
        this.userId = userId;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getUserId() {
        return userId;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
