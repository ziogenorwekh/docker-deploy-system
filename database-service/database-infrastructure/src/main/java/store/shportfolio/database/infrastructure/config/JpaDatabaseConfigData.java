package store.shportfolio.database.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "database-service.jpa.config")
public class JpaDatabaseConfigData {
    private String driverClassName;
    private String databaseUrl;
    private String username;
    private String password;
    private String hibernateAuto;
    private String showSql;
    private String packageScanArea;
    private String persistenceUnit;
}
