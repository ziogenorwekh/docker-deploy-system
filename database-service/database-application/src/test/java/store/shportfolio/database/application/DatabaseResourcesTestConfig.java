package store.shportfolio.database.application;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import store.shportfolio.database.usecase.DatabaseUseCase;

@Configuration
public class DatabaseResourcesTestConfig {

    @Bean
    public DatabaseUseCase databaseApplicationService() {
        return Mockito.mock(DatabaseUseCase.class);
    }
}
