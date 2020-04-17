package neptunilus.blueprint.sa.product.exception;

/**
 * Thrown by {@link neptunilus.blueprint.sa.product.service.CategoryService} if a category already exists during adding.
 */
public class CategoryAlreadyExistsException extends RuntimeException {

    public CategoryAlreadyExistsException(final String message) {
        super(message);
    }

}
