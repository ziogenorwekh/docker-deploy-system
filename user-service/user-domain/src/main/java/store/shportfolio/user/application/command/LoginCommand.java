package store.shportfolio.user.application.command;

public class LoginCommand {

    private String email;
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
