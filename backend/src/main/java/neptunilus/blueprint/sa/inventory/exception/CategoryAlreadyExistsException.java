package neptunilus.blueprint.sa.inventory.exception;

/**
 * Thrown by {@link neptunilus.blueprint.sa.inventory.service.CategoryService} if a category already exists during adding.
 */
public class CategoryAlreadyExistsException extends RuntimeException {

    public CategoryAlreadyExistsException(final String message) {
        super(message);
    }

}
