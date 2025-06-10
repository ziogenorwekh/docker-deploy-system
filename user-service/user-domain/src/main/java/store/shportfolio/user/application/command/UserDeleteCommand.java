package store.shportfolio.user.application.command;

import java.util.UUID;

public class UserDeleteCommand {

    private String userId;

    public UserDeleteCommand(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public static class Builder {
        private String userId;
        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }
        public UserDeleteCommand build() {
            return new UserDeleteCommand(userId);
        }
    }

    public static UserDeleteCommand.Builder builder() {
        return new UserDeleteCommand.Builder();
    }
}
