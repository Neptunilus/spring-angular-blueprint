package neptunilus.blueprint.sa.product.service;

import neptunilus.blueprint.sa.product.exception.CategoryAlreadyExistsException;
import neptunilus.blueprint.sa.product.exception.CategoryNotFoundException;
import neptunilus.blueprint.sa.product.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service for handling with {@link Category}s.
 */
@Service
public interface CategoryService {

    /**
     * Returns the available categories.
     *
     * @param search   The (optional) search string for category name.
     * @param strict   Flag, if the name should be exactly the same
     * @param pageable The pagination information
     * @return The categories
     */
    Page<Category> find(String search, boolean strict, Pageable pageable);

    /**
     * Returns the category with the given id.
     *
     * @param id The id
     * @return The category
     * @throws CategoryNotFoundException If a category with the given id is not found
     */
    Category get(UUID id) throws CategoryNotFoundException;

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
     * @param id     The id of the existing category to update
     * @param update The new data for the category
     * @throws CategoryNotFoundException      If a category with the given id is not found
     * @throws CategoryAlreadyExistsException If a category with the new name is already there
     */
    void update(UUID id, Category update) throws CategoryNotFoundException, CategoryAlreadyExistsException;

    /**
     * Deletes an existing category (if in the system).
     *
     * @param id The id of the category to remove
     */
    void delete(UUID id);

}
