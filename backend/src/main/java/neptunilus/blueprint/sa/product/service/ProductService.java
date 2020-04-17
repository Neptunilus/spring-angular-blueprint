package neptunilus.blueprint.sa.product.service;

import neptunilus.blueprint.sa.product.exception.ProductAlreadyExistsException;
import neptunilus.blueprint.sa.product.exception.ProductNotFoundException;
import neptunilus.blueprint.sa.product.model.Category;
import neptunilus.blueprint.sa.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for handling with {@link Product}s.
 */
@Service
public interface ProductService {

    /**
     * Returns the available products.
     *
     * @param search   The (optional) search string for product name
     * @param category The (optional) category of the product
     * @param pageable The pagination information
     * @return The products
     */
    Page<Product> find(String search, Category category, Pageable pageable);

    /**
     * Returns the product with the given name.
     *
     * @param name The name
     * @return The product
     * @throws ProductNotFoundException If a product with the given name is not found
     */
    Product get(String name) throws ProductNotFoundException;

    /**
     * Creates a new product.
     *
     * @param product The product to add
     * @throws ProductAlreadyExistsException If a product with the given name is already there
     */
    void create(Product product) throws ProductAlreadyExistsException;

    /**
     * Updates an existing product.
     *
     * @param name   The name of the existing product to update
     * @param update The new data for the product
     * @throws ProductNotFoundException If a product with the given name is not found
     */
    void update(String name, Product update) throws ProductNotFoundException;

    /**
     * Deletes an existing product (if in the system).
     *
     * @param name The name of the product to remove
     */
    void delete(String name);

}
