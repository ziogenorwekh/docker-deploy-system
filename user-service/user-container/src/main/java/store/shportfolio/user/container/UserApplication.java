package store.shportfolio.user.container;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@EntityScan(basePackages = "store.shportfolio.user.infrastructure")
@EnableCaching
@EnableDiscoveryClient
@ComponentScan(basePackages = "store.shportfolio.user")
@SpringBootApplication
public class UserApplication {

    public static void main(String[] args) {
        log.info("UserApplication started");
        SpringApplication.run(UserApplication.class, args);
    }
}
