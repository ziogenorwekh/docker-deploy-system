package store.shportfolio.user.infrastructure.test.respository;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import store.shportfolio.user.infrastructure.jpa.adapter.UserRepositoryImpl;
import store.shportfolio.user.infrastructure.jpa.entity.UserEntity;
import store.shportfolio.user.infrastructure.jpa.mapper.UserEntityDataMapper;
import store.shportfolio.user.infrastructure.jpa.repository.UserJpaRepository;

@EntityScan(basePackageClasses = UserEntity.class)
@EnableJpaRepositories(basePackages = "store.shportfolio.user.infrastructure")
@Configuration
public class TestJpaConfig {

    @Bean
    public UserRepositoryImpl userRepository(UserJpaRepository userJpaRepository) {
        return new UserRepositoryImpl(userJpaRepository,userEntityDataMapper());
    }

    @Bean
    public UserEntityDataMapper userEntityDataMapper() {
        return new UserEntityDataMapper();
    }
}
