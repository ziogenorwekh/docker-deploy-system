package store.shportfolio.database.application.exceptionhandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExceptionResponse {

    private final List<String> errors;
    private final LocalDateTime timestamp;
    public ExceptionResponse(List<String> errors,LocalDateTime timestamp) {
        this.errors = errors;
        this.timestamp = timestamp;
    }

    public List<String> getErrors() {
        return errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public static class Builder {
        private List<String> errors = new ArrayList<>();
        private LocalDateTime timestamp;
        public Builder error(String error) {
            errors.add(error);
            return this;
        }

        public Builder errors(List<String> errors) {
            this.errors.addAll(errors);
            return this;
        }
        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        public ExceptionResponse build() {
            return new ExceptionResponse(errors, timestamp);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

}
