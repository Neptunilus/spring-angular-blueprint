package neptunilus.blueprint.sa.security.controller.exception;

import neptunilus.blueprint.sa.common.controller.exception.ApiError;
import neptunilus.blueprint.sa.common.controller.exception.BaseExceptionHandler;
import neptunilus.blueprint.sa.security.controller.UserController;
import neptunilus.blueprint.sa.security.exception.UserAlreadyExistsException;
import neptunilus.blueprint.sa.security.exception.UserNotFoundException;
import neptunilus.blueprint.sa.security.exception.UserRoleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception handling for all security related controllers.
 */
@ControllerAdvice(basePackageClasses = UserController.class)
public class SecurityExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiError> handleUserAlreadyExists(final UserAlreadyExistsException exception) {
        return buildBasicErrorResponse(HttpStatus.CONFLICT, exception);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleUserNotFound(final UserNotFoundException exception) {
        return buildBasicErrorResponse(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleUserRoleNotFound(final UserRoleNotFoundException exception) {
        return buildBasicErrorResponse(HttpStatus.NOT_FOUND, exception);
    }

}
