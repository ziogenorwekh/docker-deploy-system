package store.shportfolio.gateway.application.config;

import lombok.Data;

@Data
public class CorsConfigData {
    private String accessControlAllowOrigin;
    private String accessControlAllowMethods;
    private String accessControlAllowHeaders;
    private String accessControlAllowCredentials;
}
