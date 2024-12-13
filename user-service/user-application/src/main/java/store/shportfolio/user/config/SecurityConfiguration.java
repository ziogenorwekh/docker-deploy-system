package store.shportfolio.user.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;
import store.shportfolio.user.oauth2handler.OAuthAuthenticationFailureHandler;
import store.shportfolio.user.oauth2handler.OAuthAuthenticationSuccessHandler;


@EnableWebSecurity
@ComponentScan(basePackages = "store.shportfolio.user")
@Configuration
public class SecurityConfiguration {


    private final GoogleOauth2ConfigData googleOauth2ConfigData;
    private final OAuthAuthenticationSuccessHandler oAuthAuthenticationSuccessHandler;
    private final OAuthAuthenticationFailureHandler oAuthAuthenticationFailureHandler;

    @Autowired
    public SecurityConfiguration(GoogleOauth2ConfigData googleOauth2ConfigData,
                                 OAuthAuthenticationSuccessHandler oAuthAuthenticationSuccessHandler,
                                 OAuthAuthenticationFailureHandler oAuthAuthenticationFailureHandler) {
        this.googleOauth2ConfigData = googleOauth2ConfigData;
        this.oAuthAuthenticationSuccessHandler = oAuthAuthenticationSuccessHandler;
        this.oAuthAuthenticationFailureHandler = oAuthAuthenticationFailureHandler;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(authorizeRequests -> {
            authorizeRequests.requestMatchers("/oauth2/authorization/google",
                    "/api/auth/**").permitAll();
            authorizeRequests.requestMatchers(HttpMethod.POST, "/api/users")
                    .permitAll().anyRequest().authenticated();
            authorizeRequests.anyRequest().authenticated();
        });
        http.oauth2Login(oauth2 -> {
            oauth2.clientRegistrationRepository(clientRegistrationRepository());
            oauth2.successHandler(oAuthAuthenticationSuccessHandler)
                    .failureHandler(oAuthAuthenticationFailureHandler);
        });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(googleClientRegistration());
    }

    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
                .clientId(googleOauth2ConfigData.getClientId())
                .clientSecret(googleOauth2ConfigData.getClientSecret())
                .scope(googleOauth2ConfigData.getScope())
                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                .tokenUri("https://oauth2.googleapis.com/token")
                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .clientName("Google")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();
    }

}
