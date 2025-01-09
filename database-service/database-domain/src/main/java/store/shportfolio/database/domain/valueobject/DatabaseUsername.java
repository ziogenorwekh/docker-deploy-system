package store.shportfolio.database.domain.valueobject;

import java.util.Objects;
import java.util.Random;

public class DatabaseUsername {
    private final String value;

    private static final int RANDOM_ALPHABET_LENGTH = 4;

    public DatabaseUsername(String value) {
        if (!isValidDatabaseUsername(value)) {
            throw new IllegalArgumentException("Invalid database username: " + value);
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DatabaseUsername fromUsername(String username) {
        String randomAlphabet = generateRandomAlphabet(RANDOM_ALPHABET_LENGTH);
        String databaseUsername = username + "_" + randomAlphabet;
        return new DatabaseUsername(databaseUsername);
    }

    private static String generateRandomAlphabet(int length) {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomString.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return randomString.toString();
    }

    private static boolean isValidDatabaseUsername(String name) {
        return name != null && name.matches("^[a-zA-Z0-9_]+$");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseUsername that = (DatabaseUsername) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}