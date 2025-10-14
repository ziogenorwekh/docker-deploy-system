package store.shportfolio.deploy.container.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import store.shportfolio.deploy.domain.DeployDomainService;
import store.shportfolio.deploy.domain.DeployDomainServiceImpl;

@Configuration
public class DeployConfig {

    @Bean
    public DeployDomainService deployDomainService() {
        return new DeployDomainServiceImpl();
    }
}
