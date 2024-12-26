package store.shportfolio.deploy.application.openfeign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "store.shportfolio.deploy.application.openfeign")
public class FeignClientConfiguration {
}
