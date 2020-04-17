package neptunilus.blueprint.sa.product.service;

import neptunilus.blueprint.sa.product.exception.CategoryAlreadyExistsException;
import neptunilus.blueprint.sa.product.exception.CategoryNotFoundException;
import neptunilus.blueprint.sa.product.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service for handling with {@link Category}s.
 */
@Service
public interface CategoryService {

    /**
     * Returns the available categories.
     *
     * @param search   The (optional) search string for category name.
     * @param pageable The pagination information
     * @return The categories
     */
    Page<Category> find(String search, Pageable pageable);

    /**
     * Returns the category with the given name.
     *
     * @param name The name
     * @return The category
     * @throws CategoryNotFoundException If a category with the given name is not found
     */
    Category get(String name) throws CategoryNotFoundException;

    /**
     * Creates a new category.
     *
     * @param category The category to add
     * @throws CategoryAlreadyExistsException If a category with the given name is already there
     */
    void create(Category category) throws CategoryAlreadyExistsException;

    /**
     * Updates an existing category.
     *
     * @param name   The name of the existing category to update
     * @param update The new data for the category
     * @throws CategoryNotFoundException If a category with the given name is not found
     */
    void update(String name, Category update) throws CategoryNotFoundException;

    /**
     * Deletes an existing category (if in the system).
     *
     * @param name The name of the category to remove
     */
    void delete(String name);

}
