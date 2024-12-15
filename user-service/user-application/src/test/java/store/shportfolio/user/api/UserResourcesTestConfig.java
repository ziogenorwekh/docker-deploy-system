package store.shportfolio.user.api;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import store.shportfolio.user.application.UserApplicationService;
import store.shportfolio.user.application.UserAuthenticationService;

@Configuration
public class UserResourcesTestConfig {


    @Bean
    public UserApplicationService userApplicationService() {
        return Mockito.mock(UserApplicationService.class);
    }

    @Bean
    public UserAuthenticationService userAuthenticationService() {
        return Mockito.mock(UserAuthenticationService.class);
    }
}
