package neptunilus.blueprint.sa.common.controller.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Base exception handling user by all exception handlers.
 */
public abstract class BaseExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiError> handleDataIntegrityViolation(final DataIntegrityViolationException exception) {
        return buildBasicErrorResponse(HttpStatus.BAD_REQUEST, exception);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException exception) {
        return buildBasicErrorResponse(HttpStatus.BAD_REQUEST, exception);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            final MethodArgumentNotValidException exception,
            final HttpHeaders headers,
            final HttpStatus status,
            final WebRequest request) {

        final ApiError apiError = new ApiError();
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> apiError.withError(fieldError.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    protected ResponseEntity<ApiError> buildBasicErrorResponse(final HttpStatus httpStatus, final Exception exception) {
        return ResponseEntity.status(httpStatus).body(new ApiError().withError(exception.getMessage()));
    }
}
