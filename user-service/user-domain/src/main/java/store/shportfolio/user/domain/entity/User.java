package store.shportfolio.user.domain.entity;

import store.shportfolio.common.domain.entitiy.AggregateRoot;
import store.shportfolio.common.domain.valueobject.Email;
import store.shportfolio.common.domain.valueobject.UserId;
import store.shportfolio.common.domain.valueobject.Username;
import store.shportfolio.user.domain.exception.DomainException;
import store.shportfolio.user.domain.valueobject.AccountStatus;
import store.shportfolio.user.domain.valueobject.Password;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class User extends AggregateRoot<UserId> {

    private final Email email;
    private final Username username;
    private Password password;
    private AccountStatus accountStatus;
    private final LocalDateTime createdAt;
    private Boolean oAuth;

    public User(UserId userId, Email email,
                Username username, Password password,
                AccountStatus accountStatus, LocalDateTime createdAt, Boolean oAuth) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
        super.setId(userId);
        this.oAuth = oAuth;
    }

    public User(String userId, String email,
                String username, String password,
                AccountStatus accountStatus, LocalDateTime createdAt) {
        this.email = new Email(email);
        this.username = new Username(username);
        this.password = new Password(password);
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
        super.setId(new UserId(userId));
    }


    public static User createUser(String newUserId, String email, String newUsername, String newPassword, Boolean oAuth) {
        isValidateEmail(email);
        isValidateUsername(newUsername);
        Password password = new Password(newPassword);
        isValidatePassword(password);

        UserId userId = new UserId(newUserId);
        Email newEmail = new Email(email);
        Username username = new Username(newUsername);

        LocalDateTime createdAt = LocalDateTime.now();
        AccountStatus newAccountStatus = AccountStatus.ENABLED;
        return new User(userId, newEmail, username, password, newAccountStatus, createdAt, oAuth);
    }

    public static User createGoogleUser(String googleUserId, String email, String newUsername, Boolean oAuth) {
        String removeWhitespace = Username.removeWhitespace(newUsername);

        UserId userId = new UserId(googleUserId);
        Email newEmail = new Email(email);
        Username username = new Username(removeWhitespace);
        Password password = new Password("");
        LocalDateTime createdAt = LocalDateTime.now();
        AccountStatus newAccountStatus = AccountStatus.ENABLED;

        return new User(userId, newEmail, username, password, newAccountStatus, createdAt, oAuth);
    }

    /**
     * @param currentPassword is raw password
     * @param newPassword     is encrypted password
     */
    public void updatePassword(String currentPassword, String newPassword) {
        if (!password.matches(currentPassword)) {
            throw new DomainException("Password does not match");
        }
        Password password = new Password(newPassword);
        if (!password.isEncrypted()) {
            throw new DomainException("Password must be encrypted");
        }
        this.password = password;
    }

    public void disableAccount() {
        this.accountStatus = AccountStatus.DISABLED;
    }


    private static void isValidateEmail(String email) {
        if (!Email.isValidEmail(email)) {
            throw new DomainException("Email is not valid");
        }
    }

    private static void isValidateUsername(String username) {
        if (!Username.isValidUsername(username)) {
            throw new DomainException("Username is not valid");
        }
    }

    private static void isValidatePassword(Password password) {
        if (!password.isEncrypted()) {
            throw new DomainException("Password must be encrypted");
        }
    }

    public Email getEmail() {
        return email;
    }

    public Username getUsername() {
        return username;
    }

    public Password getPassword() {
        return password;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Boolean getoAuth() {
        return oAuth;
    }
}
