package neptunilus.blueprint.sa.product.exception;

/**
 * Thrown by {@link neptunilus.blueprint.sa.product.service.ProductService} if a product already exists during adding.
 */
public class ProductAlreadyExistsException extends RuntimeException {

    public ProductAlreadyExistsException(final String message) {
        super(message);
    }

}
