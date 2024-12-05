package store.shportfolio.common.domain.valueobject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email {

    private final String value;
    private static final String EMAIL_PATTERN =
            "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public Email(String value) {
        this.value = value;
    }
    public static boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_PATTERN, email);
    }

    public String getValue() {
        return value;
    }
}
