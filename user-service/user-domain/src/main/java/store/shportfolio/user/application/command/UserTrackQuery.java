package store.shportfolio.user.application.command;


public class UserTrackQuery {

    private String userId;

    public UserTrackQuery() {
    }

    public UserTrackQuery(String userId) {
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
        public UserTrackQuery build() {
            return new UserTrackQuery(userId);
        }
    }

    public static UserTrackQuery.Builder builder() {
        return new UserTrackQuery.Builder();
    }
}
