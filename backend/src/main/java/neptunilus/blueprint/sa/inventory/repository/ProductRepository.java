package neptunilus.blueprint.sa.inventory.repository;

import neptunilus.blueprint.sa.inventory.model.Category;
import neptunilus.blueprint.sa.inventory.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * The repository for the {@link Product}s.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    /**
     * Returns the {@link Product} with the given name or {@link Optional#empty()} if none found.
     *
     * @param name The name
     * @return The product
     */
    Optional<Product> findOneByName(String name);

    /**
     * Returns the {@link Product} with the given name and category or {@link Optional#empty()} if none found.
     *
     * @param name     The name
     * @param category The category
     * @return The product
     */
    Optional<Product> findOneByNameAndCategory(String name, Category category);

    /**
     * Returns all {@link Product}s containing the search in name.
     *
     * @param search   The search
     * @param pageable The pagination information
     * @return The products
     */
    Page<Product> findByNameContainingIgnoreCase(String search, Pageable pageable);

    /**
     * Returns all {@link Product}s belonging to a specific {@link Category}.
     *
     * @param category The category
     * @param pageable The pagination information
     * @return The products
     */
    Page<Product> findByCategory(Category category, Pageable pageable);

    /**
     * Returns all {@link Product}s containing the search in name and belonging to a specific {@link Category}.
     *
     * @param search   The search
     * @param category The category
     * @param pageable The pagination information
     * @return The products
     */
    Page<Product> findByNameContainingIgnoreCaseAndCategory(String search, Category category, Pageable pageable);

}
