package store.shportfolio.user.usecase.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class LoginCommand {

    @NotEmpty(message = "Email must be necessary.")
    @Email
    private String email;

    @NotEmpty(message = "NewPassword must be necessary.")
    private String password;

    public LoginCommand() {
    }

    public LoginCommand(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }

    public static class Builder {
        private String email;
        private String password;
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        public Builder password(String password) {
            this.password = password;
            return this;
        }
        public LoginCommand build() {
            return new LoginCommand(email, password);
        }
    }

    public static LoginCommand.Builder builder() {
        return new LoginCommand.Builder();
    }
}
