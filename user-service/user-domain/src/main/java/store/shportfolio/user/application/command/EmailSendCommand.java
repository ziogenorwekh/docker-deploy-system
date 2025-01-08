package store.shportfolio.user.application.command;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class EmailSendCommand {

    @NotEmpty(message = "Email must be necessary.")
    @Email
    private String email;

    public EmailSendCommand() {
    }

    public EmailSendCommand(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public static class Builder {
        private String email;
        public Builder email(String email) {
            this.email = email;
            return this;
        }
        public EmailSendCommand build() {
            return new EmailSendCommand(email);
        }
    }

    public static EmailSendCommand.Builder builder() {
        return new EmailSendCommand.Builder();
    }

}
