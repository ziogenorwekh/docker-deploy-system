package store.shportfolio.user.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserCreateCommand {

    @NotEmpty(message = "Email must be necessary.")
    @Email
    private final String email;

    @NotEmpty(message = "Username must be necessary.")
    @Size(min = 4, message = "username must be at least than 4 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must contain only letters and numbers.")
    private final String username;


    @NotEmpty(message = "NewPassword must be necessary.")
    @Size(min = 4, message = "Password must be at least than 4 characters.")
    @Size(max = 10, message = "Password must not exceed 10 characters.")
    @Pattern(regexp = ".*[A-Za-z]+.*", message = "NewPassword must contain at least one letter.")
    private final String password;


    private String token;

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
}
