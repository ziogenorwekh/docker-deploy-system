package store.shportfolio.database.application.openfeign;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;

@Configuration
@EnableFeignClients(basePackages = "store.shportfolio.database.application.openfeign")
public class FeignClientConfiguration {

    @Bean
    public Jackson2JsonDecoder jackson2JsonDecoder() {
        return new Jackson2JsonDecoder();
    }

    @Bean
    public Jackson2JsonEncoder jackson2JsonEncoder() {
        return new Jackson2JsonEncoder();
    }
}
