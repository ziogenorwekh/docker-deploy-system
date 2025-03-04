package store.shportfolio.user.application.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import store.shportfolio.user.application.security.config.GoogleOauth2ConfigData;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션 비활성화
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);
        http.logout(AbstractHttpConfigurer::disable);
        http.cors(AbstractHttpConfigurer::disable);


        http.authorizeHttpRequests(authorizeRequests -> {
            authorizeRequests.anyRequest().permitAll();
        });

//        http.oauth2Login(oauth2 -> {
//            oauth2.clientRegistrationRepository(clientRegistrationRepository());
//            oauth2.userInfoEndpoint(userInfoEndpointConfig -> {
//                userInfoEndpointConfig.userService(userDefaultOAuth2UserService);
//            });
//        });

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


//    @Bean
//    public ClientRegistrationRepository clientRegistrationRepository() {
//        return new InMemoryClientRegistrationRepository(googleClientRegistration());
//    }

//    private ClientRegistration googleClientRegistration() {
//        return ClientRegistration.withRegistrationId("google")
//                .clientId(googleOauth2ConfigData.getClientId())
//                .clientSecret(googleOauth2ConfigData.getClientSecret())
//                .scope(googleOauth2ConfigData.getScope())
//                .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
//                .tokenUri("https://oauth2.googleapis.com/token")
//                .userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo")
//                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
//                .clientName("Google")
//                .userNameAttributeName("sub")
//                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
//                .build();
//    }

}
