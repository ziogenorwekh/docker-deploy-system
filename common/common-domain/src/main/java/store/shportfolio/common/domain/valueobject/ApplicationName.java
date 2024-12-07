package store.shportfolio.common.domain.valueobject;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplicationName {

    private final String applicationName;
    private static final String APPLICATION_NAME_PATTERN = "^[a-zA-Z]+$";

    public ApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public boolean isValidApplicationName(String applicationName) {
        Pattern pattern = Pattern.compile(APPLICATION_NAME_PATTERN);
        Matcher matcher = pattern.matcher(applicationName);
        return matcher.matches();  // 조건에 맞으면 true 반환
    }

    public String getApplicationName() {
        return applicationName;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ApplicationName that = (ApplicationName) o;
        return Objects.equals(applicationName, that.applicationName);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(applicationName);
    }
}
