package neptunilus.blueprint.sa.inventory.service;

import neptunilus.blueprint.sa.inventory.exception.ProductAlreadyExistsException;
import neptunilus.blueprint.sa.inventory.exception.ProductNotFoundException;
import neptunilus.blueprint.sa.inventory.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for handling with {@link Product}s.
 */
@Service
public interface ProductService {

    /**
     * Returns the available products.
     *
     * @param search     The (optional) search string for product name
     * @param strict     Flag, if the name should be exactly the same
     * @param categoryId The (optional) category (id) of the product
     * @param pageable   The pagination information
     * @return The products
     */
    @PreAuthorize("hasAuthority('READ_PRODUCT')")
    Page<Product> find(String search, boolean strict, UUID categoryId, Pageable pageable);

    /**
     * Returns the product with the given id.
     *
     * @param id The id
     * @return The product
     * @throws ProductNotFoundException If a product with the given id is not found
     */
    @PreAuthorize("hasAuthority('READ_PRODUCT')")
    Product get(UUID id) throws ProductNotFoundException;

    /**
     * Creates a new product.
     *
     * @param product The product to add
     * @return The id of the new product
     * @throws ProductAlreadyExistsException If a product with the given name is already there
     */
    @PreAuthorize("hasAuthority('CREATE_PRODUCT')")
    UUID create(Product product) throws ProductAlreadyExistsException;

    /**
     * Updates an existing product.
     *
     * @param id     The id of the existing product to update
     * @param update The new data for the product
     * @throws ProductNotFoundException      If a product with the given id is not found
     * @throws ProductAlreadyExistsException If a product with the new name is already there
     */
    @PreAuthorize("hasAuthority('UPDATE_PRODUCT')")
    void update(UUID id, Product update) throws ProductNotFoundException, ProductAlreadyExistsException;

    /**
     * Deletes an existing product (if in the system).
     *
     * @param id The id of the product to remove
     */
    @PreAuthorize("hasAuthority('DELETE_PRODUCT')")
    void delete(UUID id);

}
