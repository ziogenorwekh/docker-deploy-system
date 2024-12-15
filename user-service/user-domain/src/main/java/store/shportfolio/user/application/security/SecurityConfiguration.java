package store.shportfolio.user.application.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.web.SecurityFilterChain;
import store.shportfolio.user.application.security.config.GoogleOauth2ConfigData;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {


    private final GoogleOauth2ConfigData googleOauth2ConfigData;
    private final UserDefaultOAuth2UserService userDefaultOAuth2UserService;

    @Autowired
    public SecurityConfiguration(GoogleOauth2ConfigData googleOauth2ConfigData,
                                 UserDefaultOAuth2UserService userDefaultOAuth2UserService) {
        this.googleOauth2ConfigData = googleOauth2ConfigData;
        this.userDefaultOAuth2UserService = userDefaultOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.logout(AbstractHttpConfigurer::disable);
        // 인증이 필요한 요청들에 대해 404 반환 설정
        http.authorizeHttpRequests(authorizeRequests -> {
            authorizeRequests.requestMatchers(HttpMethod.GET, "/status").permitAll();
            authorizeRequests.requestMatchers(HttpMethod.POST, "/api/users").permitAll();
            authorizeRequests.requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll();
            authorizeRequests.requestMatchers("/oauth2/authorization/google").permitAll();
            authorizeRequests.anyRequest().authenticated();
        });
        http.oauth2Login(oauth2 -> {
            oauth2.clientRegistrationRepository(clientRegistrationRepository());
            oauth2.userInfoEndpoint(userInfoEndpointConfig -> {
                userInfoEndpointConfig.userService(userDefaultOAuth2UserService);
            });
        });

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        ProviderManager authenticationManager = (ProviderManager) authenticationConfiguration
                .getAuthenticationManager();
        return authenticationManager;
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
                .userNameAttributeName("sub")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();
    }

}
