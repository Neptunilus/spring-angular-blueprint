package neptunilus.blueprint.sa.inventory.exception;

/**
 * Thrown by {@link neptunilus.blueprint.sa.inventory.service.ProductService} if a product was not found.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(final String message) {
        super(message);
    }

}
