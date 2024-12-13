package store.shportfolio.user.api;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import store.shportfolio.user.application.UserApplicationService;
import store.shportfolio.user.application.UserAuthenticationService;

@Configuration
public class UserResourcesConfig {


    @Bean
    public UserApplicationService userApplicationService() {
        return Mockito.mock(UserApplicationService.class);
    }

    @Bean
    public UserAuthenticationService userAuthenticationService() {
        return Mockito.mock(UserAuthenticationService.class);
    }
}
