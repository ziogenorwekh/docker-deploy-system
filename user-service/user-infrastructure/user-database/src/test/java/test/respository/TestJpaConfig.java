package test.respository;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import store.shportfolio.user.infrastructure.database.jpa.adapter.UserRepositoryImpl;
import store.shportfolio.user.infrastructure.database.jpa.entity.UserEntity;
import store.shportfolio.user.infrastructure.database.jpa.mapper.UserEntityDataAccessMapper;
import store.shportfolio.user.infrastructure.database.jpa.repository.UserJpaRepository;

@EntityScan(basePackageClasses = UserEntity.class)
@EnableJpaRepositories(basePackages = "store.shportfolio.user.infrastructure")
@Configuration
public class TestJpaConfig {

    @Bean
    public UserRepositoryImpl userRepository(UserJpaRepository userJpaRepository) {
        return new UserRepositoryImpl(userJpaRepository,userEntityDataMapper());
    }

    @Bean
    public UserEntityDataAccessMapper userEntityDataMapper() {
        return new UserEntityDataAccessMapper();
    }
}
