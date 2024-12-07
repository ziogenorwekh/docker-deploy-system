package store.shportfolio.user.application.command;

public class EmailSendCommand {

    private final String email;

    public EmailSendCommand(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
