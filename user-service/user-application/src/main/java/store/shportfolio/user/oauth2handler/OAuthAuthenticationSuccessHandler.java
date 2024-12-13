package store.shportfolio.user.oauth2handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import store.shportfolio.common.domain.valueobject.Token;
import store.shportfolio.user.application.jwt.JwtHandler;

import java.io.IOException;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    @Value("front.url")
    private String redirectUri;

    private final JwtHandler jwtHandler;

    @Autowired
    public OAuthAuthenticationSuccessHandler(JwtHandler jwtHandler) {
        this.jwtHandler = jwtHandler;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String googleId = oAuth2User.getAttribute("sub");

        Token loginToken = jwtHandler.createLoginToken(email, googleId);

        response.addHeader("Authorization", "Bearer " + loginToken.getValue());
        response.sendRedirect(redirectUri);
    }
}
