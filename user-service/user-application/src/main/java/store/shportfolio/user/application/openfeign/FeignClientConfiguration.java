package store.shportfolio.user.application.openfeign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "store.shportfolio.user.application.openfeign")
public class FeignClientConfiguration {
}
