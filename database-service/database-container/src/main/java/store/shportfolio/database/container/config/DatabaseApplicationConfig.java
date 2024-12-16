package store.shportfolio.database.container.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import store.shportfolio.database.domain.DatabaseDomainService;
import store.shportfolio.database.domain.DatabaseDomainServiceImpl;

@Configuration
public class DatabaseApplicationConfig {


    @Bean
    public DatabaseDomainService databaseDomainService() {
        return new DatabaseDomainServiceImpl();
    }

}
