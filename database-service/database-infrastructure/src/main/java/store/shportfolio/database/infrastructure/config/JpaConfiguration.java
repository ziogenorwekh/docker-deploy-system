package store.shportfolio.database.infrastructure.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitManager;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import store.shportfolio.database.infrastructure.entity.DatabaseEntity;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "store.shportfolio.database.infrastructure.repository"
)
@EntityScan(basePackages = "store.shportfolio.database.infrastructure.entity")
public class JpaConfiguration {

    private JpaDatabaseConfigData jpaDatabaseConfigData;

    public JpaConfiguration(JpaDatabaseConfigData jpaDatabaseConfigData) {
        this.jpaDatabaseConfigData = jpaDatabaseConfigData;
    }

    @Primary
    @Bean(name = "JpaDataSource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .password(jpaDatabaseConfigData.getPassword())
                .url(jpaDatabaseConfigData.getDatabaseUrl())
                .username(jpaDatabaseConfigData.getUsername())
                .driverClassName(jpaDatabaseConfigData.getDriverClassName())
                .build();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("JpaDataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", jpaDatabaseConfigData.getHibernateAuto());
        properties.put("hibernate.show_sql", jpaDatabaseConfigData.getShowSql());
        return builder
                .dataSource(dataSource)
                .packages(jpaDatabaseConfigData.getPackageScanArea()) // Update with your domain package
                .persistenceUnit(jpaDatabaseConfigData.getPersistenceUnit())
                .properties(properties)
                .build();
    }

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder() {
        return new EntityManagerFactoryBuilder(new HibernateJpaVendorAdapter(), new HashMap<>(), null);
    }

    @Primary
    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(
            @Qualifier("entityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
