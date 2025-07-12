package store.shportfolio.user.infrastructure.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomCacheManager {

    private final CacheManager cacheManager;

    @Autowired
    public CustomCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public Boolean save(String email, String code) {
        Cache cache = cacheManager.getCache("verificationCodes");
        if (cache.get(email, String.class) != null) {
            return false;
        }
        cache.put(email, code);
        return true;
    }

    public Optional<String> getCode(String email) {
        Cache cache = cacheManager.getCache("verificationCodes");
        String code = cache.get(email, String.class);
        if (code != null) {
            return Optional.of(code);
        } else {
            return Optional.empty();
        }
    }

    public void clear() {
        cacheManager.getCache("verificationCodes").clear();
    }
}
