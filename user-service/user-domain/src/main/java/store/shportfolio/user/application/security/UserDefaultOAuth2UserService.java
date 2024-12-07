package store.shportfolio.user.application.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import store.shportfolio.user.application.ports.output.repository.UserRepository;
import store.shportfolio.user.domain.UserDomainService;
import store.shportfolio.user.domain.entity.User;

import java.util.Collections;
import java.util.Map;

@Component
public class UserDefaultOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final UserDomainService userDomainService;

    public UserDefaultOAuth2UserService(UserRepository userRepository, UserDomainService userDomainService) {
        this.userRepository = userRepository;
        this.userDomainService = userDomainService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");
        String googleId = (String) attributes.get("sub");

        if (userRepository.findByEmail(email).isEmpty()) {
            User googleUser = userDomainService.createGoogleUser(googleId, email, name);
            userRepository.save(googleUser);
        }

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("USER")),
                attributes,
                "name"
        );
    }
}
