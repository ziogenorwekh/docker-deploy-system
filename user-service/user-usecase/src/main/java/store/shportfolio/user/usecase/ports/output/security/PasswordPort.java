package store.shportfolio.user.usecase.ports.output.security;

public interface PasswordPort {

    String encode(String password);
}
