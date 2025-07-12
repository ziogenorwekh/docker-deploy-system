package store.shportfolio.user.infrastructure.security.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import store.shportfolio.user.usecase.ports.output.security.PasswordPort;

@Component
public class PasswordAdapter implements PasswordPort {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }
}
