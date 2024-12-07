package store.shportfolio.user.application.command;

public class OAuthLoginCommand {
    private final String token;

    public OAuthLoginCommand(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
