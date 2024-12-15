package store.shportfolio.database.application;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseResourcesTestConfig {

    @Bean
    public DatabaseApplicationService databaseApplicationService() {
        return Mockito.mock(DatabaseApplicationService.class);
    }
}
