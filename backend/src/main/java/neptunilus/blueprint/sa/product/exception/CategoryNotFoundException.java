package neptunilus.blueprint.sa.product.exception;

/**
 * Thrown by {@link neptunilus.blueprint.sa.product.service.CategoryService} if a category was not found.
 */
public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(final String message) {
        super(message);
    }

}
