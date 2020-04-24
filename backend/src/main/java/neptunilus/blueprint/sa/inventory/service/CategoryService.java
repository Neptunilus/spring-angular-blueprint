package neptunilus.blueprint.sa.inventory.service;

import neptunilus.blueprint.sa.inventory.exception.CategoryAlreadyExistsException;
import neptunilus.blueprint.sa.inventory.exception.CategoryNotFoundException;
import neptunilus.blueprint.sa.inventory.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('READ_CATEGORY')")
    Page<Category> find(String search, boolean strict, Pageable pageable);

    /**
     * Returns the category with the given id.
     *
     * @param id The id
     * @return The category
     * @throws CategoryNotFoundException If a category with the given id is not found
     */
    @PreAuthorize("hasAuthority('READ_CATEGORY')")
    Category get(UUID id) throws CategoryNotFoundException;

    /**
     * Creates a new category.
     *
     * @param category The category to add
     * @return The id of the new category
     * @throws CategoryAlreadyExistsException If a category with the given name is already there
     */
    @PreAuthorize("hasAuthority('CREATE_CATEGORY')")
    UUID create(Category category) throws CategoryAlreadyExistsException;

    /**
     * Updates an existing category.
     *
     * @param id     The id of the existing category to update
     * @param update The new data for the category
     * @throws CategoryNotFoundException      If a category with the given id is not found
     * @throws CategoryAlreadyExistsException If a category with the new name is already there
     */
    @PreAuthorize("hasAuthority('UPDATE_CATEGORY')")
    void update(UUID id, Category update) throws CategoryNotFoundException, CategoryAlreadyExistsException;

    /**
     * Deletes an existing category (if in the system).
     *
     * @param id The id of the category to remove
     */
    @PreAuthorize("hasAuthority('DELETE_CATEGORY')")
    void delete(UUID id);

}
