package neptunilus.blueprint.sa.product.exception;

/**
 * Thrown by {@link neptunilus.blueprint.sa.product.service.ProductService} if a product was not found.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(final String message) {
        super(message);
    }

}
