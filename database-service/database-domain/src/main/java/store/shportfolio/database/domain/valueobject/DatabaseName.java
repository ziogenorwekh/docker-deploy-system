package store.shportfolio.database.domain.valueobject;

import java.util.Objects;
import java.util.Random;

public class DatabaseName {
    private final String value;

    private static final int RANDOM_ALPHABET_LENGTH = 6;

    public DatabaseName(String value) {
        if (!isValidDatabaseName(value)) {
            throw new IllegalArgumentException("Invalid database name: " + value);
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DatabaseName fromUsername(String username) {
        String randomAlphabet = generateRandomAlphabet(RANDOM_ALPHABET_LENGTH);
        String databaseName = username + "_" + randomAlphabet;
        return new DatabaseName(databaseName);
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

    private static boolean isValidDatabaseName(String name) {
        return name != null && name.matches("^[a-zA-Z0-9_]+$");
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseName that = (DatabaseName) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}