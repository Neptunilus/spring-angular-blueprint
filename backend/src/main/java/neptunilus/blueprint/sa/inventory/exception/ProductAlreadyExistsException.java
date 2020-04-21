package neptunilus.blueprint.sa.inventory.exception;

/**
 * Thrown by {@link neptunilus.blueprint.sa.inventory.service.ProductService} if a product already exists during adding.
 */
public class ProductAlreadyExistsException extends RuntimeException {

    public ProductAlreadyExistsException(final String message) {
        super(message);
    }

}
