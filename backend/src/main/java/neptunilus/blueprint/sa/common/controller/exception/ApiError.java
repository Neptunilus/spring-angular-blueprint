package neptunilus.blueprint.sa.common.controller.exception;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Error object returned to the caller in case of an API error.
 */
public class ApiError {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final List<String> errors = new LinkedList<>();

    public ApiError withError(final String error) {
        this.errors.add(error);
        return this;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public List<String> getErrors() {
        return this.errors;
    }

}
