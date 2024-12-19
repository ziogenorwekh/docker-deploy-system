package store.shportfolio.common.domain.valueobject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationName {

    private final String value;
    private static final String APPLICATION_NAME_PATTERN = "^[a-zA-Z]+$";

    public ApplicationName(String value) {
        this.value = value;
    }

    public static boolean isValidApplicationName(String applicationName) {
        Pattern pattern = Pattern.compile(APPLICATION_NAME_PATTERN);
        Matcher matcher = pattern.matcher(applicationName);
        return matcher.matches();  // 조건에 맞으면 true 반환
    }

    public String getValue() {
        return value;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationName that = (ApplicationName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
