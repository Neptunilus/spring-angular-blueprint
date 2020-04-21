package neptunilus.blueprint.sa.inventory.exception;

/**
 * Thrown by {@link neptunilus.blueprint.sa.inventory.service.CategoryService} if a category was not found.
 */
public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(final String message) {
        super(message);
    }

}
