package store.shportfolio.user.container.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import store.shportfolio.user.application.ports.output.repository.UserRepository;
import store.shportfolio.user.domain.UserDomainService;
import store.shportfolio.user.domain.UserDomainServiceImpl;
import store.shportfolio.user.infrastructure.jpa.adapter.UserRepositoryImpl;

@Configuration
public class UserApplicationConfig {

    @Bean
    public UserDomainService userDomainService() {
        return new UserDomainServiceImpl();
    }

}
