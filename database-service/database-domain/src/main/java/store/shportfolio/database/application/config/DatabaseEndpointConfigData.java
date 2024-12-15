package store.shportfolio.database.application.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "database-service.endpoint")
public class DatabaseEndpointConfigData {

    private String endpointUrl;
}
