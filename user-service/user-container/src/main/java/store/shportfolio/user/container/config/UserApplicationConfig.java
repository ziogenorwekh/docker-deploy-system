package store.shportfolio.user.container.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import store.shportfolio.user.domain.UserDomainService;
import store.shportfolio.user.domain.UserDomainServiceImpl;

@Configuration
public class UserApplicationConfig {

    @Bean
    public UserDomainService userDomainService() {
        return new UserDomainServiceImpl();
    }

}
