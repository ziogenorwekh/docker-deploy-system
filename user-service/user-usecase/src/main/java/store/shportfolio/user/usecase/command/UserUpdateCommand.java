package store.shportfolio.user.usecase.command;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserUpdateCommand {

    private String userId;
    @NotEmpty(message = "CurrentPassword must be necessary.")
    @Size(min = 4, message = "Password must be at least than 4 characters.")
    @Size(max = 15, message = "Password must not exceed 10 characters.")
    @Pattern(regexp = ".*[A-Za-z]+.*", message = "password must contain at least one letter.")
    private String currentPassword;
    @NotEmpty(message = "NewPassword must be necessary.")
    @Size(min = 4, message = "Password must be at least than 4 characters.")
    @Size(max = 15, message = "Password must not exceed 10 characters.")
    @Pattern(regexp = ".*[A-Za-z]+.*", message = "password must contain at least one letter.")
    private String newPassword;

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

    public static class Builder {
        private String userId;
        private String currentPassword;
        private String newPassword;
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }
        public Builder currentPassword(String currentPassword) {
            this.currentPassword = currentPassword;
            return this;
        }
        public Builder newPassword(String newPassword) {
            this.newPassword = newPassword;
            return this;
        }
        public UserUpdateCommand build() {
            return new UserUpdateCommand(userId, currentPassword, newPassword);
        }
    }

    public static UserUpdateCommand.Builder builder() {
        return new UserUpdateCommand.Builder();
    }
}
