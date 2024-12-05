package store.shportfolio.common.domain.valueobject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email {

    private final String value;
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@(.+)$";
    public Email(String value) {
        this.value = value;
    }
    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();  // 이메일 형식이 맞으면 true 반환
    }

    public String getValue() {
        return value;
    }
}
