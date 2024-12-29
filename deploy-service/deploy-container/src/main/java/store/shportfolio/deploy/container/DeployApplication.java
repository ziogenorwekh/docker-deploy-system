package store.shportfolio.deploy.container;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan(basePackages = "store.shportfolio.deploy")
@EntityScan(basePackages = "store.shportfolio.deploy.infrastructure.jpa")
@EnableDiscoveryClient
@SpringBootApplication
public class DeployApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeployApplication.class, args);
    }
}
