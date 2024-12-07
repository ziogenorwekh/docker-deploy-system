package store.shportfolio.user.application.command;

public class EmailVerificationCommand {

    private final String email;
    private final String code;

    public EmailVerificationCommand(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public String getEmail() {
        return email;
    }

    public String getCode() {
        return code;
    }
}
