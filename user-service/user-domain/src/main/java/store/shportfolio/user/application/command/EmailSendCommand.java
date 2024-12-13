package store.shportfolio.user.application.command;


public class EmailSendCommand {

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
