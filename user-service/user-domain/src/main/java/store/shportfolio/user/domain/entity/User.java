package store.shportfolio.user.domain.entity;

import store.shportfolio.common.domain.entitiy.AggregateRoot;
import store.shportfolio.common.domain.valueobject.Email;
import store.shportfolio.common.domain.valueobject.UserId;
import store.shportfolio.common.domain.valueobject.Username;
import store.shportfolio.user.domain.exception.DomainException;
import store.shportfolio.user.domain.valueobject.AccountStatus;
import store.shportfolio.user.domain.valueobject.Password;

import java.time.LocalDateTime;
import java.util.UUID;

public class User extends AggregateRoot<UserId> {

    private final Email email;
    private final Username username;
    private Password password;
    private AccountStatus accountStatus;
    private final LocalDateTime createdAt;

    public User(UserId userId, Email email,
                Username username, Password password,
                AccountStatus accountStatus, LocalDateTime createdAt) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.accountStatus = accountStatus;
        this.createdAt = createdAt;
        super.setId(userId);
    }


    public static User createUser(UUID newUserId, String email, String newUsername, String newPassword) {
        isValidateEmail(email);
        isValidateUsername(newUsername);
        Password password = new Password(newPassword);
        isValidatePassword(password);

        UserId userId = new UserId(newUserId);
        Email newEmail = new Email(email);
        Username username = new Username(newUsername);

        LocalDateTime createdAt = LocalDateTime.now();
        AccountStatus newAccountStatus = AccountStatus.ENABLED;
        return new User(userId, newEmail, username, password, newAccountStatus, createdAt);
    }

    /**
     * @param currentPassword is raw password
     * @param newPassword is encrypted password
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
}
