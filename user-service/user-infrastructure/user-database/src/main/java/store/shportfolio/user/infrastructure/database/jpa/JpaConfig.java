package store.shportfolio.user.infrastructure.database.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "store.shportfolio.user.infrastructure.database.jpa")
public class JpaConfig {
}
