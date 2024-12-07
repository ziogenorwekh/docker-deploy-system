package store.shportfolio.user.application.command;


public class UserTrackQuery {

    private final String userId;

    public UserTrackQuery(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
