package store.shportfolio.database.application.openfeign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "store.shportfolio.database.application.openfeign")
public class FeignClientConfiguration {
}
