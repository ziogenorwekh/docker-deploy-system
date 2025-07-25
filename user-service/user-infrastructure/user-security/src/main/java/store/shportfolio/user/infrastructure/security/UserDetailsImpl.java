package store.shportfolio.user.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.domain.valueobject.AccountStatus;

import java.util.ArrayList;
import java.util.Collection;

public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        return authorities;
    }

    public String getId() {
        return user.getId().getValue();
    }

    public String getEmail() {
        return user.getEmail().getValue();
    }

    @Override
    public String getPassword() {
        return user.getPassword().getValue();
    }

    @Override
    public String getUsername() {
        return user.getUsername().getValue();
    }

    @Override
    public boolean isEnabled() {
        return user.getAccountStatus().equals(AccountStatus.ENABLED);
    }



}
