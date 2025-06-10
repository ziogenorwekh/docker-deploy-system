package store.shportfolio.database.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class JdbcConfiguration {

    private JdbcDatabaseConfigData jdbcDatabaseConfigData;

    public JdbcConfiguration(JdbcDatabaseConfigData jdbcDatabaseConfigData) {
        this.jdbcDatabaseConfigData = jdbcDatabaseConfigData;
    }

    @Bean(name = "JdbcDataSource")
    public DataSource dataSource() {
        DataSource dataSource = DataSourceBuilder.create()
                .password(jdbcDatabaseConfigData.getPassword())
                .url(jdbcDatabaseConfigData.getDatabaseUrl())
                .username(jdbcDatabaseConfigData.getUsername())
                .driverClassName(jdbcDatabaseConfigData.getDriverClassName())
                .build();
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(@Qualifier("JdbcDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
