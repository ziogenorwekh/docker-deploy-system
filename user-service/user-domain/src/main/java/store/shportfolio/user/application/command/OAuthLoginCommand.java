package store.shportfolio.user.application.command;

public class OAuthLoginCommand {
    private String token;

    public OAuthLoginCommand(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public static class Builder {
        private String token;
        public Builder token(String token) {
            this.token = token;
            return this;
        }
        public OAuthLoginCommand build() {
            return new OAuthLoginCommand(token);
        }
    }
    public static OAuthLoginCommand.Builder builder() {
        return new OAuthLoginCommand.Builder();
    }
}
