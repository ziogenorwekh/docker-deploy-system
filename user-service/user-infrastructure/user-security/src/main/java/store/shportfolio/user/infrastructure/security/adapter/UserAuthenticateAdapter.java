package store.shportfolio.user.infrastructure.security.adapter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.user.infrastructure.security.UserDetailsImpl;
import store.shportfolio.user.usecase.dto.UserAuthenticationDataDto;
import store.shportfolio.user.usecase.exception.LoginException;
import store.shportfolio.user.usecase.ports.output.security.JwtPort;
import store.shportfolio.user.usecase.ports.output.security.UserAuthenticatePort;

@Slf4j
@Component
public class UserAuthenticateAdapter implements UserAuthenticatePort {

    private final AuthenticationManager authenticationManager;
    private final JwtPort jwtPort;

    @Autowired
    public UserAuthenticateAdapter(AuthenticationManager authenticationManager, JwtPort jwtPort) {
        this.authenticationManager = authenticationManager;
        this.jwtPort = jwtPort;
    }

    @Override
    public UserAuthenticationDataDto authenticate(String email, String password) {
        UsernamePasswordAuthenticationToken token;

        UserDetailsImpl userDetails;
        try {
            token = new UsernamePasswordAuthenticationToken(email, password);
            Authentication authenticate = authenticationManager.authenticate(token);
            userDetails = (UserDetailsImpl) authenticate.getPrincipal();
            log.info("login access user -> {}", userDetails.getEmail());
            Token jwtToken = jwtPort.createLoginToken(userDetails.getEmail(), userDetails.getUsername(), userDetails.getId());
            return UserAuthenticationDataDto.builder()
                    .userId(userDetails.getId())
                    .email(userDetails.getEmail())
                    .oauth(false)
                    .token(jwtToken.getValue())
                    .build();
        } catch (BadCredentialsException e) {
            log.error("BadCredentialsException -> {}", e.getMessage());
            throw new BadCredentialsException("Login failed", e);
        } catch (DisabledException e) {
            log.error("DisabledException -> {}", e.getMessage());
            throw new DisabledException("The account is disabled", e);
        } catch (Exception e) {
            log.error("Exception -> {}", e.getMessage());
            throw new LoginException("An unexpected error occurred", e);
        }
    }
}
