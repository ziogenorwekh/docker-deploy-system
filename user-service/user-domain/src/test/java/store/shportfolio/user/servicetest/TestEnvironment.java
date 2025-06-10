package store.shportfolio.user.servicetest;

import java.util.HashMap;
import java.util.Map;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;

public class TestEnvironment implements Environment {

    private final Map<String, String> properties = new HashMap<>();

    public TestEnvironment() {
        // 테스트 환경 변수 초기화
        properties.put("server.token.secret", "test-secret");
        properties.put("server.token.email.expiration", "3600");
        properties.put("server.token.login.expiration", "54000");
    }

    @Override
    public String[] getActiveProfiles() {
        return new String[]{"test"};
    }

    @Override
    public String[] getDefaultProfiles() {
        return new String[]{"default"};
    }

    @Override
    public boolean acceptsProfiles(String... profiles) {
        for (String profile : profiles) {
            if ("test".equals(profile)) return true;
        }
        return false;
    }

    @Override
    public boolean acceptsProfiles(Profiles profiles) {
        return profiles.matches(profile -> "test".equals(profile));
    }

    @Override
    public boolean containsProperty(String key) {
        return properties.containsKey(key);
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType) {
        String value = properties.get(key);
        if (value == null) return null;
        return convertValue(value, targetType);
    }

    @Override
    public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
        String value = properties.get(key);
        if (value == null) return defaultValue;
        return convertValue(value, targetType);
    }

    @Override
    public String getRequiredProperty(String key) throws IllegalStateException {
        if (!properties.containsKey(key)) {
            throw new IllegalStateException("Required property " + key + " not found");
        }
        return properties.get(key);
    }

    @Override
    public <T> T getRequiredProperty(String key, Class<T> targetType) throws IllegalStateException {
        String value = getRequiredProperty(key);
        return convertValue(value, targetType);
    }

    @Override
    public String resolvePlaceholders(String text) {
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            text = text.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return text;
    }

    @Override
    public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
        String resolved = resolvePlaceholders(text);
        if (resolved.contains("${")) {
            throw new IllegalArgumentException("Could not resolve placeholders in text: " + text);
        }
        return resolved;
    }

    // 유틸리티 메서드: 문자열을 원하는 타입으로 변환
    private <T> T convertValue(String value, Class<T> targetType) {
        if (targetType == String.class) {
            return targetType.cast(value);
        } else if (targetType == Integer.class || targetType == int.class) {
            return targetType.cast(Integer.parseInt(value));
        } else if (targetType == Long.class || targetType == long.class) {
            return targetType.cast(Long.parseLong(value));
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return targetType.cast(Boolean.parseBoolean(value));
        }
        throw new IllegalArgumentException("Unsupported target type: " + targetType.getName());
    }
}