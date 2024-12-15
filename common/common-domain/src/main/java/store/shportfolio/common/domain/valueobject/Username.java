package store.shportfolio.common.domain.valueobject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Username {

    private final String value;

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9]+$";

    public Username(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isValidUsername(String username) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();  // 조건에 맞으면 true 반환
    }

    public static String removeWhitespace(String username) {
        if (username == null) {
            throw new IllegalArgumentException("Username cannot be null");
        }
        return username.replaceAll("\\s+", ""); // 모든 공백 제거
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Username username = (Username) o;
        return Objects.equals(value, username.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
