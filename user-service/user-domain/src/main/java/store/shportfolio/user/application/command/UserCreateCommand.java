package store.shportfolio.user.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserCreateCommand {

    @NotEmpty(message = "Email must be necessary.")
    @Email
    private String email;

    @NotEmpty(message = "Username must be necessary.")
    @Size(min = 4, message = "username must be at least than 4 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must contain only letters and numbers.")
    private String username;


    @NotEmpty(message = "NewPassword must be necessary.")
    @Size(min = 4, message = "Password must be at least than 4 characters.")
    @Size(max = 15, message = "Password must not exceed 10 characters.")
    @Pattern(regexp = ".*[A-Za-z]+.*", message = "password must contain at least one letter.")
    private String password;


    private String token;

    public UserCreateCommand() {
    }

    public UserCreateCommand(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public static class Builder {
        private String email;
        private String username;
        private String password;
        public Builder() {

        }
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        public Builder username(String username) {
            this.username = username;
            return this;
        }
        public Builder password(String password) {
            this.password = password;
            return this;
        }
        public UserCreateCommand build() {
            return new UserCreateCommand(email, username, password);
        }
    }
    public static UserCreateCommand.Builder builder() {
        return new UserCreateCommand.Builder();
    }
}
