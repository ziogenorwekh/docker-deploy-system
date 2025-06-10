package store.shportfolio.user.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class EmailVerificationCommand {

    @NotEmpty(message = "Email must be necessary.")
    @Email
    private String email;

    @NotEmpty(message = "Code must be necessary.")
    private String code;


    public EmailVerificationCommand() {
    }

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

    public static class Builder {
        private String email;
        private String code;
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        public Builder code(String code) {
            this.code = code;
            return this;
        }
        public EmailVerificationCommand build() {
            return new EmailVerificationCommand(email, code);
        }
    }

    public static EmailVerificationCommand.Builder builder() {
        return new EmailVerificationCommand.Builder();
    }
}
