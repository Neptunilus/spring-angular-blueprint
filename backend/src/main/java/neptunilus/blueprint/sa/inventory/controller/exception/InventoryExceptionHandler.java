package neptunilus.blueprint.sa.inventory.controller.exception;

import neptunilus.blueprint.sa.common.controller.exception.ApiError;
import neptunilus.blueprint.sa.common.controller.exception.BaseExceptionHandler;
import neptunilus.blueprint.sa.inventory.controller.ProductController;
import neptunilus.blueprint.sa.inventory.exception.CategoryAlreadyExistsException;
import neptunilus.blueprint.sa.inventory.exception.CategoryNotFoundException;
import neptunilus.blueprint.sa.inventory.exception.ProductAlreadyExistsException;
import neptunilus.blueprint.sa.inventory.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Exception handling for all inventory related controllers.
 */
@ControllerAdvice(basePackageClasses = ProductController.class)
public class InventoryExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ApiError> handleCategoryAlreadyExists(final CategoryAlreadyExistsException exception) {
        return buildBasicErrorResponse(HttpStatus.CONFLICT, exception);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleCategoryNotFound(final CategoryNotFoundException exception) {
        return buildBasicErrorResponse(HttpStatus.NOT_FOUND, exception);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleProductAlreadyExists(final ProductAlreadyExistsException exception) {
        return buildBasicErrorResponse(HttpStatus.CONFLICT, exception);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleProductNotFound(final ProductNotFoundException exception) {
        return buildBasicErrorResponse(HttpStatus.NOT_FOUND, exception);
    }

}
